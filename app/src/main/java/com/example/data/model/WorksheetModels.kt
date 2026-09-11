package com.example.data.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class QuestionType(val displayName: String) {
    MULTIPLE_CHOICE("Multiple Choice"),
    FILL_IN_BLANK("Fill-in-the-Blanks"),
    SHORT_ANSWER("Sentence Transformation")
}

enum class DifficultyLevel(val displayName: String, val colorHex: Long) {
    BEGINNER("Beginner (A1-A2)", 0xFF10B981),
    INTERMEDIATE("Intermediate (B1-B2)", 0xFF3B82F6),
    ADVANCED("Advanced (C1-C2)", 0xFF8B5CF6)
}

enum class OutputStructure(val displayName: String, val description: String) {
    EXERCISES_ONLY("Exercises only", "Generates interactive practice questions directly."),
    EXPLANATION_AND_EXERCISES("Explanation + Exercises", "Includes a concise grammar theory explanation before exercises."),
    MULTIPLE_CHOICE_QUIZ("Multiple Choice Quiz", "Generates exclusively multiple-choice format questions.")
}

enum class GrammarTopic(
    val id: String,
    val title: String,
    val description: String,
    val sampleSnippet: String
) {
    VERB_TENSES(
        "verb_tenses",
        "Verb Tenses & Aspects",
        "Master Past, Present, Future, and Continuous/Perfect aspects in context.",
        "<h2>Lesson 4: Past Perfect vs Past Simple</h2><p>When two actions happened in the past, use the <b>past perfect</b> for the earlier action.</p>"
    ),
    PREPOSITIONS(
        "prepositions",
        "Prepositions of Time & Place",
        "Accurate usage of in, on, at, through, despite, and spatial prepositions.",
        "<div><h3>Preposition Guide</h3><p>Use <b>at</b> for specific times, <b>on</b> for days/dates, and <b>in</b> for long periods.</p></div>"
    ),
    CONDITIONALS(
        "conditionals",
        "Conditionals (0, 1st, 2nd, 3rd)",
        "Hypothetical reasoning, future possibilities, and past regrets.",
        "<section><h4>Conditional Clauses</h4><p>If + Past Simple -> Would + Infinitive (2nd Conditional for unreal situations).</p></section>"
    ),
    PASSIVE_VOICE(
        "passive_voice",
        "Active & Passive Voice",
        "Shifting sentence focus, agentless passive constructions, and formal writing.",
        "<article><h3>Passive Transformation</h3><p>The active object becomes the passive subject: <i>The dog bit the man -> The man was bitten by the dog.</i></p></article>"
    ),
    MODAL_VERBS(
        "modal_verbs",
        "Modal Verbs (Must, Should, Could)",
        "Expressing obligation, permission, logical deduction, and polite requests.",
        "<div class='grammar-box'><b>Modals of Deduction:</b> Must have + past participle for strong past certainty.</div>"
    ),
    SUBJECT_VERB_AGREEMENT(
        "subject_verb",
        "Subject-Verb Agreement",
        "Singular vs plural subjects, collective nouns, and compound clause rules.",
        "<p>Neither the teacher nor the students <u>were</u> aware of the rule changes.</p>"
    ),
    HTML_CUSTOM(
        "html_custom",
        "Raw HTML Analysis",
        "Extract grammar concepts automatically from imported web articles or HTML markup.",
        "<!DOCTYPE html><html><body><h1>Grammar Excerpt</h1><p>The report had been completed before the audit commenced.</p></body></html>"
    )
}

data class QuestionItem(
    val id: String,
    val type: QuestionType,
    val questionText: String,
    val options: List<String> = emptyList(),
    val correctAnswer: String,
    val explanation: String,
    val hint: String
)

data class UserSubscription(
    val isPremium: Boolean = false,
    val dailyLimit: Int = 3,
    val generationsUsedToday: Int = 1,
    val userName: String = "Mr.Zaafouri Abdelmalek",
    val userRole: String = "English Department Chair",
    val schoolName: String = "Oakwood Academy"
)
