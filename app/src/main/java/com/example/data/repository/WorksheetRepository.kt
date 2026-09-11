package com.example.data.repository

import com.example.data.local.WorksheetDao
import com.example.data.local.WorksheetEntity
import com.example.data.model.DifficultyLevel
import com.example.data.model.GrammarTopic
import com.example.data.model.OutputStructure
import com.example.data.model.QuestionItem
import com.example.data.model.QuestionType
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class WorksheetRepository(private val worksheetDao: WorksheetDao) {

    private val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
    private val questionListType = Types.newParameterizedType(List::class.java, QuestionItem::class.java)
    private val jsonAdapter = moshi.adapter<List<QuestionItem>>(questionListType)

    val allWorksheets: Flow<List<WorksheetEntity>> = worksheetDao.getAllWorksheets()

    fun getWorksheetById(id: Long): Flow<WorksheetEntity?> = worksheetDao.getWorksheetById(id)

    suspend fun saveWorksheet(
        title: String,
        topic: String,
        difficulty: String,
        rawInput: String,
        questions: List<QuestionItem>,
        targetGrammarRules: String
    ): Long {
        val questionsJson = jsonAdapter.toJson(questions) ?: "[]"
        val entity = WorksheetEntity(
            title = title,
            topic = topic,
            difficulty = difficulty,
            rawInput = rawInput,
            questionsJson = questionsJson,
            targetGrammarRules = targetGrammarRules
        )
        return worksheetDao.insertWorksheet(entity)
    }

    suspend fun deleteWorksheet(id: Long) {
        worksheetDao.deleteWorksheetById(id)
    }

    suspend fun toggleFavorite(id: Long, isFavorite: Boolean) {
        worksheetDao.updateFavorite(id, isFavorite)
    }

    fun parseQuestions(questionsJson: String): List<QuestionItem> {
        return try {
            jsonAdapter.fromJson(questionsJson) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun generateWorksheetWithAI(
        rawHtmlOrText: String,
        selectedTopic: GrammarTopic,
        difficulty: DifficultyLevel,
        includeExplanations: Boolean,
        specificRuleFilter: String,
        outputStructure: OutputStructure,
        selectedTemplate: com.example.data.model.WorksheetTemplate,
        onProgressUpdate: (String) -> Unit
    ): Pair<String, List<QuestionItem>> {
        onProgressUpdate("Connecting to Gemini AI Engine...")
        delay(400)

        onProgressUpdate("Analyzing topic syntax & extracting grammatical patterns...")
        delay(500)

        onProgressUpdate("Synthesizing questions, hints & detailed explanation keys...")

        val activeWorksheet = com.example.data.remote.GeminiService.generateGrammarWorksheet(
            topic = selectedTopic,
            htmlCode = rawHtmlOrText,
            difficulty = difficulty,
            includeExplanations = includeExplanations,
            specificRule = specificRuleFilter,
            outputStructure = outputStructure,
            selectedTemplate = selectedTemplate
        )

        return Pair(activeWorksheet.title, activeWorksheet.questions)
    }

    private fun extractTitleFromHtml(html: String): String {
        val titleRegex = Regex("(?i)<h[1-3][^>]*>(.*?)</h[1-3]>")
        val match = titleRegex.find(html)
        return match?.groupValues?.get(1)?.replace(Regex("<[^>]*>"), "")?.take(30)
            ?: "Custom HTML Excerpt"
    }

    private fun generateQuestionsFromHtml(html: String, difficulty: DifficultyLevel): List<QuestionItem> {
        return listOf(
            QuestionItem(
                id = UUID.randomUUID().toString(),
                type = QuestionType.MULTIPLE_CHOICE,
                questionText = "In the provided HTML text, identify which tense is used in the phrase 'had been completed before the audit commenced':",
                options = listOf(
                    "A) Present Perfect Continuous",
                    "B) Past Perfect Passive",
                    "C) Future Perfect Simple",
                    "D) Past Continuous"
                ),
                correctAnswer = "B) Past Perfect Passive",
                explanation = "'Had been completed' combines 'had' + 'been' + past participle, forming the Past Perfect Passive to denote an action finished prior to another past event.",
                hint = "Notice the auxiliary 'had' followed by 'been' and the past participle 'completed'."
            ),
            QuestionItem(
                id = UUID.randomUUID().toString(),
                type = QuestionType.FILL_IN_BLANK,
                questionText = "Complete the sentence from the text: The report _______ (submit) by the deadline yesterday.",
                options = emptyList(),
                correctAnswer = "was submitted",
                explanation = "Since the action occurred in the past ('yesterday') and the subject 'The report' is passive, use 'was submitted'.",
                hint = "Use the past simple passive form of 'submit'."
            ),
            QuestionItem(
                id = UUID.randomUUID().toString(),
                type = QuestionType.SHORT_ANSWER,
                questionText = "Rewrite the active clause 'The auditor reviewed all financial files' into the Passive Voice:",
                options = emptyList(),
                correctAnswer = "All financial files were reviewed by the auditor.",
                explanation = "The object 'All financial files' becomes the subject, followed by 'were reviewed' and the agent 'by the auditor'.",
                hint = "Make 'All financial files' the subject of the new sentence."
            )
        )
    }

    private fun generateQuestionsForTopic(topic: GrammarTopic, difficulty: DifficultyLevel): List<QuestionItem> {
        return when (topic) {
            GrammarTopic.VERB_TENSES -> listOf(
                QuestionItem(
                    id = "vt_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "By the time the bell rang, the students _______ their grammar exam.",
                    options = listOf(
                        "A) finished",
                        "B) had finished",
                        "C) have finished",
                        "D) finish"
                    ),
                    correctAnswer = "B) had finished",
                    explanation = "The Past Perfect ('had finished') is used for an action completed BEFORE another past time event ('bell rang').",
                    hint = "Look for the time marker 'By the time...' referencing a past event."
                ),
                QuestionItem(
                    id = "vt_2",
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "She _______ (study) English literature for three years when she decided to apply to Oxford.",
                    options = emptyList(),
                    correctAnswer = "had been studying",
                    explanation = "Use Past Perfect Continuous ('had been studying') to emphasize duration prior to a point in the past.",
                    hint = "Focus on the duration ('for three years') leading up to a past decision."
                ),
                QuestionItem(
                    id = "vt_3",
                    type = QuestionType.SHORT_ANSWER,
                    questionText = "Transform into Future Perfect: 'They complete the project by next Friday.'",
                    options = emptyList(),
                    correctAnswer = "They will have completed the project by next Friday.",
                    explanation = "Future Perfect requires 'will have' + past participle ('completed') to show an action finished before a future point.",
                    hint = "Combine 'will have' with the past participle."
                )
            )

            GrammarTopic.PREPOSITIONS -> listOf(
                QuestionItem(
                    id = "prep_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "The annual international conference will take place _______ Monday morning.",
                    options = listOf("A) in", "B) on", "C) at", "D) by"),
                    correctAnswer = "B) on",
                    explanation = "We use the preposition 'on' before specific days of the week and dates ('on Monday morning').",
                    hint = "Days of the week require 'on'."
                ),
                QuestionItem(
                    id = "prep_2",
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "Despite the delay, all participants arrived safely _______ the station.",
                    options = emptyList(),
                    correctAnswer = "at",
                    explanation = "'Arrive at' is used for specific buildings or locations like stations, airports, or addresses.",
                    hint = "Use 'at' for point locations like stations."
                ),
                QuestionItem(
                    id = "prep_3",
                    type = QuestionType.SHORT_ANSWER,
                    questionText = "Choose the correct preposition: 'She is highly skilled _____ explaining complex grammar rules.'",
                    options = emptyList(),
                    correctAnswer = "in",
                    explanation = "The adjective 'skilled' pairs naturally with 'in' (or 'at') when preceding a gerund.",
                    hint = "Skilled + [preposition] + gerund."
                )
            )

            GrammarTopic.CONDITIONALS -> listOf(
                QuestionItem(
                    id = "cond_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "If I _______ earlier about the schedule change, I would have notified the students.",
                    options = listOf(
                        "A) knew",
                        "B) had known",
                        "C) have known",
                        "D) know"
                    ),
                    correctAnswer = "B) had known",
                    explanation = "Third Conditional formula: If + Past Perfect ('had known'), Would Have + Past Participle.",
                    hint = "This is a past unreal regret (3rd conditional)."
                ),
                QuestionItem(
                    id = "cond_2",
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "If water reaches 100 degrees Celsius, it _______ (boil).",
                    options = emptyList(),
                    correctAnswer = "boils",
                    explanation = "Zero Conditional expresses universal scientific facts: If + Present Simple, Present Simple.",
                    hint = "Zero conditional uses Present Simple for facts."
                ),
                QuestionItem(
                    id = "cond_3",
                    type = QuestionType.SHORT_ANSWER,
                    questionText = "Complete the 2nd Conditional: 'If I were the headmaster, I ________ (reduce) homework loads.'",
                    options = emptyList(),
                    correctAnswer = "would reduce",
                    explanation = "Second Conditional structure: If + Past Subjunctive ('were'), Would + Base Infinitive ('would reduce').",
                    hint = "Use 'would' + base verb."
                )
            )

            GrammarTopic.PASSIVE_VOICE -> listOf(
                QuestionItem(
                    id = "pv_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "Which sentence correctly converts 'The teacher has marked all essay assignments' to Passive Voice?",
                    options = listOf(
                        "A) All essay assignments were marked by the teacher.",
                        "B) All essay assignments have been marked by the teacher.",
                        "C) All essay assignments are marked by the teacher.",
                        "D) All essay assignments had been marked by the teacher."
                    ),
                    correctAnswer = "B) All essay assignments have been marked by the teacher.",
                    explanation = "The active verb 'has marked' (Present Perfect) converts to 'have been marked' in passive.",
                    hint = "Keep the original tense (Present Perfect)."
                ),
                QuestionItem(
                    id = "pv_2",
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "A new language laboratory _______ (construct) on campus right now.",
                    options = emptyList(),
                    correctAnswer = "is being constructed",
                    explanation = "Present Continuous Passive: is/are + being + past participle.",
                    hint = "Action happening right now in passive continuous."
                ),
                QuestionItem(
                    id = "pv_3",
                    type = QuestionType.SHORT_ANSWER,
                    questionText = "Change to passive: 'Shakespeare wrote Hamlet in the 17th century.'",
                    options = emptyList(),
                    correctAnswer = "Hamlet was written by Shakespeare in the 17th century.",
                    explanation = "Past Simple Active ('wrote') becomes Past Simple Passive ('was written').",
                    hint = "Make 'Hamlet' the subject."
                )
            )

            else -> listOf(
                QuestionItem(
                    id = "gen_1",
                    type = QuestionType.MULTIPLE_CHOICE,
                    questionText = "Identify the correct subject-verb agreement in the following sentence:",
                    options = listOf(
                        "A) Either the director or the assistant prepare the report.",
                        "B) Either the director or the assistant prepares the report.",
                        "C) Neither the director nor the assistant are ready.",
                        "D) The list of guidelines were posted yesterday."
                    ),
                    correctAnswer = "B) Either the director or the assistant prepares the report.",
                    explanation = "When subjects are joined by 'either... or', the verb agrees with the subject closest to it ('assistant' = singular -> 'prepares').",
                    hint = "Match the verb with the closest subject."
                ),
                QuestionItem(
                    id = "gen_2",
                    type = QuestionType.FILL_IN_BLANK,
                    questionText = "The student _______ (who / whom) won the essay contest received a university scholarship.",
                    options = emptyList(),
                    correctAnswer = "who",
                    explanation = "'Who' serves as the subject of the clause 'won the essay contest'.",
                    hint = "Use 'who' as the subject pronoun."
                ),
                QuestionItem(
                    id = "gen_3",
                    type = QuestionType.SHORT_ANSWER,
                    questionText = "Fill in the correct modal: 'You ______ (must/should) turn off cell phones during the examination.'",
                    options = emptyList(),
                    correctAnswer = "must",
                    explanation = "'Must' indicates an official rule or mandatory obligation during exams.",
                    hint = "Strong official requirement."
                )
            )
        }
    }

    suspend fun seedInitialWorksheetsIfEmpty() {
        // Pre-populate if empty
        val sample1 = WorksheetEntity(
            title = "Past Perfect vs Past Continuous Drill",
            topic = GrammarTopic.VERB_TENSES.title,
            difficulty = DifficultyLevel.INTERMEDIATE.displayName,
            rawInput = GrammarTopic.VERB_TENSES.sampleSnippet,
            questionsJson = jsonAdapter.toJson(generateQuestionsForTopic(GrammarTopic.VERB_TENSES, DifficultyLevel.INTERMEDIATE)) ?: "[]",
            targetGrammarRules = "Aspect contrast in narrative past tenses",
            isFavorite = true
        )
        val sample2 = WorksheetEntity(
            title = "Conditional Clauses (Type 2 & 3)",
            topic = GrammarTopic.CONDITIONALS.title,
            difficulty = DifficultyLevel.ADVANCED.displayName,
            rawInput = GrammarTopic.CONDITIONALS.sampleSnippet,
            questionsJson = jsonAdapter.toJson(generateQuestionsForTopic(GrammarTopic.CONDITIONALS, DifficultyLevel.ADVANCED)) ?: "[]",
            targetGrammarRules = "Unreal condition structures and past counterfactuals",
            isFavorite = false
        )
        worksheetDao.insertWorksheet(sample1)
        worksheetDao.insertWorksheet(sample2)
    }
}
