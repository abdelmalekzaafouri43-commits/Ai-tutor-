package com.example.data.remote

import com.example.BuildConfig
import com.example.data.model.DifficultyLevel
import com.example.data.model.GrammarTopic
import com.example.data.model.OutputStructure
import com.example.data.model.QuestionItem
import com.example.data.model.QuestionType
import com.example.ui.viewmodel.ActiveWorksheet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID
import java.util.concurrent.TimeUnit

object GeminiService {
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun generateGrammarWorksheet(
        topic: GrammarTopic,
        htmlCode: String,
        difficulty: DifficultyLevel,
        includeExplanations: Boolean,
        specificRule: String,
        outputStructure: OutputStructure
    ): ActiveWorksheet = withContext(Dispatchers.IO) {
        val apiKey = try { BuildConfig.GEMINI_API_KEY } catch (e: Exception) { "" }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            // Key is not set or placeholder, fallback smoothly to local generator
            return@withContext generateFallbackWorksheet(topic, htmlCode, difficulty, includeExplanations, specificRule, outputStructure)
        }

        val promptText = buildString {
            append("You are an expert ESL/EFL English Grammar Worksheet Creator. ")
            when (outputStructure) {
                OutputStructure.EXERCISES_ONLY -> {
                    append("Generate a worksheet focusing strictly on practice exercises. Do not include any lesson introductions or explanations before the questions. ")
                }
                OutputStructure.EXPLANATION_AND_EXERCISES -> {
                    append("Generate a worksheet that includes a short theory explanation or lesson summary before the exercises. ")
                }
                OutputStructure.MULTIPLE_CHOICE_QUIZ -> {
                    append("Generate a multiple choice quiz where EVERY question is of type MULTIPLE_CHOICE. Do not generate fill-in-the-blank or sentence transformation questions. All 4 questions must be multiple-choice with 4 options each. ")
                }
            }
            append("Generate an interactive 4-question grammar practice worksheet ")
            if (htmlCode.isNotBlank()) {
                append("analyzing and extracting text content from this HTML snippet: \n```html\n$htmlCode\n```\n")
            } else {
                append("for the grammar topic: ${topic.title} (${topic.description}). ")
            }
            append("Target difficulty level: ${difficulty.displayName}. ")
            if (specificRule.isNotBlank()) {
                append("Specific focus rule/filter: $specificRule. ")
            }
            append("Return ONLY a valid JSON object matching this strict schema, with no markdown formatting or extra text outside JSON:\n")
            append("""
                {
                  "title": "Worksheet Title Here",
                  "questions": [
                    {
                      "id": "q1",
                      "type": "MULTIPLE_CHOICE",
                      "questionText": "Question statement here...",
                      "options": ["Option A", "Option B", "Option C", "Option D"],
                      "correctAnswer": "Option A",
                      "explanation": "Detailed grammatical explanation why this is correct.",
                      "hint": "Useful hint for the student"
                    },
                    {
                      "id": "q2",
                      "type": "FILL_IN_BLANK",
                      "questionText": "Fill in the blank statement...",
                      "options": [],
                      "correctAnswer": "correct word",
                      "explanation": "Detailed explanation.",
                      "hint": "Hint here"
                    },
                    {
                      "id": "q3",
                      "type": "SHORT_ANSWER",
                      "questionText": "Rewrite sentence using requested tense/structure...",
                      "options": [],
                      "correctAnswer": "rewritten sentence",
                      "explanation": "Detailed explanation.",
                      "hint": "Hint"
                    },
                    {
                      "id": "q4",
                      "type": "MULTIPLE_CHOICE",
                      "questionText": "Another grammar question...",
                      "options": ["Opt1", "Opt2", "Opt3", "Opt4"],
                      "correctAnswer": "Opt1",
                      "explanation": "Explanation.",
                      "hint": "Hint"
                    }
                  ]
                }
            """.trimIndent())
        }

        try {
            val jsonBody = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().put("text", promptText))
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)

                // Enforce JSON response format
                val genConfig = JSONObject().apply {
                    val responseFormat = JSONObject().apply {
                        put("responseMimeType", "application/json")
                    }
                    put("responseFormat", responseFormat)
                }
                put("generationConfig", genConfig)
            }

            val request = Request.Builder()
                .url("$BASE_URL?key=$apiKey")
                .post(jsonBody.toString().toRequestBody("application/json; charset=utf-8".toMediaType()))
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful || responseString.isBlank()) {
                return@withContext generateFallbackWorksheet(topic, htmlCode, difficulty, includeExplanations, specificRule, outputStructure)
            }

            val responseJson = JSONObject(responseString)
            val candidates = responseJson.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text") ?: ""

            if (text.isBlank()) {
                return@withContext generateFallbackWorksheet(topic, htmlCode, difficulty, includeExplanations, specificRule, outputStructure)
            }

            // Clean markdown code blocks if present
            val cleanJson = text.trim()
                .removePrefix("```json")
                .removePrefix("```")
                .removeSuffix("```")
                .trim()

            val parsedObj = JSONObject(cleanJson)
            val title = parsedObj.optString("title", "${topic.title} Practice Sheet")
            val jsonQuestions = parsedObj.optJSONArray("questions") ?: JSONArray()

            val questionItems = mutableListOf<QuestionItem>()
            for (i in 0 until jsonQuestions.length()) {
                val qObj = jsonQuestions.getJSONObject(i)
                val typeStr = qObj.optString("type", "MULTIPLE_CHOICE")
                val qType = when (typeStr) {
                    "FILL_IN_BLANK" -> QuestionType.FILL_IN_BLANK
                    "SHORT_ANSWER" -> QuestionType.SHORT_ANSWER
                    else -> QuestionType.MULTIPLE_CHOICE
                }

                val optionsArray = qObj.optJSONArray("options")
                val optionsList = mutableListOf<String>()
                if (optionsArray != null) {
                    for (j in 0 until optionsArray.length()) {
                        optionsList.add(optionsArray.getString(j))
                    }
                }

                questionItems.add(
                    QuestionItem(
                        id = qObj.optString("id", UUID.randomUUID().toString()),
                        type = qType,
                        questionText = qObj.optString("questionText", "Grammar Practice Question"),
                        options = optionsList,
                        correctAnswer = qObj.optString("correctAnswer", ""),
                        explanation = if (includeExplanations) qObj.optString("explanation", "Standard grammar rule application.") else "Explanation hidden for standard tier.",
                        hint = qObj.optString("hint", "Focus on verb form and context clues.")
                    )
                )
            }

            if (questionItems.isEmpty()) {
                return@withContext generateFallbackWorksheet(topic, htmlCode, difficulty, includeExplanations, specificRule, outputStructure)
            }

            ActiveWorksheet(
                dbId = null,
                title = title,
                topic = topic.title,
                difficulty = difficulty.displayName,
                questions = questionItems,
                rawInput = htmlCode,
                targetRules = specificRule,
                isSaved = false
            )

        } catch (e: Exception) {
            e.printStackTrace()
            generateFallbackWorksheet(topic, htmlCode, difficulty, includeExplanations, specificRule, outputStructure)
        }
    }

    private fun generateFallbackWorksheet(
        topic: GrammarTopic,
        htmlCode: String,
        difficulty: DifficultyLevel,
        includeExplanations: Boolean,
        specificRule: String,
        outputStructure: OutputStructure
    ): ActiveWorksheet {
        val questions = when (topic) {
            GrammarTopic.VERB_TENSES -> listOf(
                QuestionItem(
                    id = "v1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "By the time the class ended, Sarah _____ all three grammar exercises.",
                    options = listOf("had completed", "completes", "was complete", "has completing"),
                    correctAnswer = "had completed",
                    explanation = "Use Past Perfect ('had completed') to describe an action finished before another past event.",
                    hint = "Look for 'By the time...'"
                ),
                QuestionItem(
                    id = "v2",
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "They _____ (study) English for five years before moving to London.",
                    options = emptyList(),
                    correctAnswer = "had been studying",
                    explanation = "Past Perfect Continuous emphasizes ongoing duration leading up to a point in the past.",
                    hint = "Past Perfect Continuous form: had + been + verb-ing"
                )
            )
            GrammarTopic.PREPOSITIONS -> listOf(
                QuestionItem(
                    id = "p1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "The teacher insisted _____ completing the homework before Friday.",
                    options = listOf("on", "in", "at", "with"),
                    correctAnswer = "on",
                    explanation = "The verb 'insist' is followed by the preposition 'on'.",
                    hint = "Insist + preposition + gerund"
                ),
                QuestionItem(
                    id = "p2",
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "She has been interested _____ linguistics since high school.",
                    options = emptyList(),
                    correctAnswer = "in",
                    explanation = "The adjective 'interested' takes the preposition 'in'.",
                    hint = "Interested + preposition"
                )
            )
            else -> listOf(
                QuestionItem(
                    id = "g1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "If he _____ harder, he would have passed the advanced English examination.",
                    options = listOf("had studied", "studied", "has studied", "would study"),
                    correctAnswer = "had studied",
                    explanation = "Third Conditional uses 'If + Past Perfect' for hypothetical past scenarios.",
                    hint = "Third conditional structure: If + had + past participle"
                ),
                QuestionItem(
                    id = "g2",
                    type = QuestionType.SHORT_ANSWER,
                    questionText = "Rewrite in Passive Voice: 'The AI tutor generated twenty grammar worksheets.'",
                    options = emptyList(),
                    correctAnswer = "Twenty grammar worksheets were generated by the AI tutor.",
                    explanation = "In Passive Voice, the object becomes the subject: Object + was/were + past participle + by + agent.",
                    hint = "Start with 'Twenty grammar worksheets...'"
                )
            )
        }

        val processedQuestions = if (outputStructure == OutputStructure.MULTIPLE_CHOICE_QUIZ) {
            questions.map { q ->
                if (q.type != QuestionType.MULTIPLE_CHOICE) {
                    q.copy(
                        type = QuestionType.MULTIPLE_CHOICE,
                        options = listOf(q.correctAnswer, "Incorrect Option B", "Incorrect Option C", "Incorrect Option D").shuffled()
                    )
                } else {
                    q
                }
            }
        } else {
            questions
        }

        return ActiveWorksheet(
            dbId = null,
            title = if (htmlCode.isNotBlank()) "HTML Derived Grammar Sheet" else "${topic.title} Interactive Practice",
            topic = topic.title,
            difficulty = difficulty.displayName,
            questions = processedQuestions,
            rawInput = htmlCode,
            targetRules = specificRule,
            isSaved = false
        )
    }
}
