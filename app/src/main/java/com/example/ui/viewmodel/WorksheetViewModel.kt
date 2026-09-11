package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.WorksheetEntity
import com.example.data.model.DifficultyLevel
import com.example.data.model.GrammarTopic
import com.example.data.model.OutputStructure
import com.example.data.model.QuestionItem
import com.example.data.model.UserSubscription
import com.example.data.repository.WorksheetRepository
import com.example.ui.navigation.NavItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class ActiveWorksheet(
    val dbId: Long? = null,
    val title: String,
    val topic: String,
    val difficulty: String,
    val questions: List<QuestionItem>,
    val rawInput: String = "",
    val targetRules: String = "",
    val isSaved: Boolean = false
)

class WorksheetViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WorksheetRepository

    val savedWorksheets: StateFlow<List<WorksheetEntity>>

    // Navigation & Layout
    private val _currentNavItem = MutableStateFlow(NavItem.DASHBOARD)
    val currentNavItem: StateFlow<NavItem> = _currentNavItem.asStateFlow()

    private val _isSidebarExpanded = MutableStateFlow(true)
    val isSidebarExpanded: StateFlow<Boolean> = _isSidebarExpanded.asStateFlow()

    // Theme Selection
    private val _selectedTheme = MutableStateFlow(com.example.ui.theme.AppTheme.SAPPHIRE)
    val selectedTheme: StateFlow<com.example.ui.theme.AppTheme> = _selectedTheme.asStateFlow()

    fun setSelectedTheme(theme: com.example.ui.theme.AppTheme) {
        _selectedTheme.value = theme
    }

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }

    // Subscription & Paywall
    private val _subscription = MutableStateFlow(UserSubscription(isPremium = false, dailyLimit = 3, generationsUsedToday = 1))
    val subscription: StateFlow<UserSubscription> = _subscription.asStateFlow()

    private val _showPaywallModal = MutableStateFlow(false)
    val showPaywallModal: StateFlow<Boolean> = _showPaywallModal.asStateFlow()

    private val _paywallReason = MutableStateFlow("Upgrade to AI Worksheet Tutor Premium for unlimited access!")
    val paywallReason: StateFlow<String> = _paywallReason.asStateFlow()

    // Generator Form State
    private val _inputMode = MutableStateFlow("TOPIC") // "TOPIC" or "HTML"
    val inputMode: StateFlow<String> = _inputMode.asStateFlow()

    private val _rawHtmlInput = MutableStateFlow("")
    val rawHtmlInput: StateFlow<String> = _rawHtmlInput.asStateFlow()

    private val _selectedTopic = MutableStateFlow(GrammarTopic.VERB_TENSES)
    val selectedTopic: StateFlow<GrammarTopic> = _selectedTopic.asStateFlow()

    private val _selectedDifficulty = MutableStateFlow(DifficultyLevel.INTERMEDIATE)
    val selectedDifficulty: StateFlow<DifficultyLevel> = _selectedDifficulty.asStateFlow()

    private val _includeExplanations = MutableStateFlow(true)
    val includeExplanations: StateFlow<Boolean> = _includeExplanations.asStateFlow()

    private val _specificRuleFilter = MutableStateFlow("Focus on active vs passive transformations")
    val specificRuleFilter: StateFlow<String> = _specificRuleFilter.asStateFlow()

    private val _outputStructure = MutableStateFlow(OutputStructure.EXPLANATION_AND_EXERCISES)
    val outputStructure: StateFlow<OutputStructure> = _outputStructure.asStateFlow()

    private val _selectedTemplate = MutableStateFlow(com.example.data.model.WorksheetTemplate.MIXED_FORMAT)
    val selectedTemplate: StateFlow<com.example.data.model.WorksheetTemplate> = _selectedTemplate.asStateFlow()

    fun setSelectedTemplate(template: com.example.data.model.WorksheetTemplate) {
        _selectedTemplate.value = template
        when (template) {
            com.example.data.model.WorksheetTemplate.MULTIPLE_CHOICE -> {
                _outputStructure.value = OutputStructure.MULTIPLE_CHOICE_QUIZ
            }
            com.example.data.model.WorksheetTemplate.FILL_IN_BLANKS -> {
                _outputStructure.value = OutputStructure.EXERCISES_ONLY
            }
            com.example.data.model.WorksheetTemplate.SENTENCE_CORRECTION -> {
                _outputStructure.value = OutputStructure.EXERCISES_ONLY
            }
            com.example.data.model.WorksheetTemplate.MIXED_FORMAT -> {
                _outputStructure.value = OutputStructure.EXPLANATION_AND_EXERCISES
            }
        }
    }

    // Generation Execution
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _generationProgressMessage = MutableStateFlow("")
    val generationProgressMessage: StateFlow<String> = _generationProgressMessage.asStateFlow()

    // Active Preview
    private val _activeWorksheet = MutableStateFlow<ActiveWorksheet?>(null)
    val activeWorksheet: StateFlow<ActiveWorksheet?> = _activeWorksheet.asStateFlow()

    // Interactive Quiz State
    private val _userAnswers = MutableStateFlow<Map<String, String>>(emptyMap())
    val userAnswers: StateFlow<Map<String, String>> = _userAnswers.asStateFlow()

    private val _showResults = MutableStateFlow(false)
    val showResults: StateFlow<Boolean> = _showResults.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = WorksheetRepository(database.worksheetDao())
        savedWorksheets = repository.allWorksheets.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            val currentList = repository.allWorksheets.first()
            if (currentList.isEmpty()) {
                repository.seedInitialWorksheetsIfEmpty()
            }
        }
    }

    fun navigateTo(navItem: NavItem) {
        _currentNavItem.value = navItem
    }

    fun toggleSidebar() {
        _isSidebarExpanded.value = !_isSidebarExpanded.value
    }

    fun setInputMode(mode: String) {
        _inputMode.value = mode
    }

    fun setRawHtmlInput(text: String) {
        _rawHtmlInput.value = text
    }

    fun setSelectedTopic(topic: GrammarTopic) {
        _selectedTopic.value = topic
    }

    fun setSelectedDifficulty(difficulty: DifficultyLevel) {
        _selectedDifficulty.value = difficulty
    }

    fun setIncludeExplanations(include: Boolean) {
        // If free and trying to enable premium detailed explanations
        if (include && !_subscription.value.isPremium) {
            triggerPaywall("Detailed Answer Keys & Explanations are a Premium feature.")
            return
        }
        _includeExplanations.value = include
    }

    fun setSpecificRuleFilter(filter: String) {
        _specificRuleFilter.value = filter
    }

    fun setOutputStructure(structure: OutputStructure) {
        _outputStructure.value = structure
    }

    fun triggerPaywall(reason: String) {
        _paywallReason.value = reason
        _showPaywallModal.value = true
    }

    fun dismissPaywall() {
        _showPaywallModal.value = false
    }

    fun toggleSubscriptionTier() {
        val current = _subscription.value
        val newTier = !current.isPremium
        _subscription.value = current.copy(
            isPremium = newTier,
            generationsUsedToday = if (newTier) 1 else current.generationsUsedToday
        )
        if (newTier) {
            _showPaywallModal.value = false
        }
    }

    fun startNewGeneration() {
        // Check free tier daily limit
        val sub = _subscription.value
        if (!sub.isPremium && sub.generationsUsedToday >= sub.dailyLimit) {
            triggerPaywall("You have reached your free daily limit of ${sub.dailyLimit} generations. Upgrade to Premium for unlimited AI worksheets!")
            return
        }

        viewModelScope.launch {
            _isGenerating.value = true
            _userAnswers.value = emptyMap()
            _showResults.value = false

            val (title, questions) = repository.generateWorksheetWithAI(
                rawHtmlOrText = if (_inputMode.value == "HTML") _rawHtmlInput.value else "",
                selectedTopic = _selectedTopic.value,
                difficulty = _selectedDifficulty.value,
                includeExplanations = _includeExplanations.value,
                specificRuleFilter = _specificRuleFilter.value,
                outputStructure = _outputStructure.value,
                selectedTemplate = _selectedTemplate.value,
                onProgressUpdate = { msg -> _generationProgressMessage.value = msg }
            )

            // Increment usage
            _subscription.value = sub.copy(generationsUsedToday = sub.generationsUsedToday + 1)

            val generated = ActiveWorksheet(
                title = title,
                topic = if (_inputMode.value == "HTML") "HTML Code Analysis" else _selectedTopic.value.title,
                difficulty = _selectedDifficulty.value.displayName,
                questions = questions,
                rawInput = if (_inputMode.value == "HTML") _rawHtmlInput.value else _selectedTopic.value.sampleSnippet,
                targetRules = _specificRuleFilter.value,
                isSaved = false
            )

            _activeWorksheet.value = generated
            _isGenerating.value = false
            _currentNavItem.value = NavItem.PRACTICE
        }
    }

    fun saveCurrentWorksheetToLibrary() {
        val current = _activeWorksheet.value ?: return
        if (current.isSaved) return

        viewModelScope.launch {
            val newId = repository.saveWorksheet(
                title = current.title,
                topic = current.topic,
                difficulty = current.difficulty,
                rawInput = current.rawInput,
                questions = current.questions,
                targetGrammarRules = current.targetRules
            )
            _activeWorksheet.value = current.copy(dbId = newId, isSaved = true)
        }
    }

    fun loadSavedWorksheet(entity: WorksheetEntity) {
        val questions = repository.parseQuestions(entity.questionsJson)
        _activeWorksheet.value = ActiveWorksheet(
            dbId = entity.id,
            title = entity.title,
            topic = entity.topic,
            difficulty = entity.difficulty,
            questions = questions,
            rawInput = entity.rawInput,
            targetRules = entity.targetGrammarRules,
            isSaved = true
        )
        _userAnswers.value = emptyMap()
        _showResults.value = false
        _currentNavItem.value = NavItem.PRACTICE
    }

    fun parseQuestions(json: String): List<QuestionItem> {
        return repository.parseQuestions(json)
    }

    fun deleteWorksheet(id: Long) {
        viewModelScope.launch {
            repository.deleteWorksheet(id)
            if (_activeWorksheet.value?.dbId == id) {
                _activeWorksheet.value = null
            }
        }
    }

    fun toggleFavorite(id: Long, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavorite(id, isFavorite)
        }
    }

    fun recordAnswer(questionId: String, answer: String) {
        val current = _userAnswers.value.toMutableMap()
        current[questionId] = answer
        _userAnswers.value = current
    }

    fun submitQuiz() {
        _showResults.value = true
    }

    fun resetQuiz() {
        _userAnswers.value = emptyMap()
        _showResults.value = false
    }

    fun loadSampleHtml() {
        _inputMode.value = "HTML"
        _rawHtmlInput.value = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <title>Advanced English Syntax Lesson</title>
            </head>
            <body>
                <div class="grammar-container">
                    <h1>Passive Voice & Past Perfect Structures</h1>
                    <p class="excerpt">
                        By the time the headmaster arrived at the hall, the examination papers 
                        <b>had already been distributed</b> to all candidate students. 
                        Although several questions <i>were considered</i> particularly challenging, 
                        all candidates completed the paper within the allotted two hours.
                    </p>
                    <div class="rule-box">
                        <h3>Key Grammar Rule:</h3>
                        <p>When reporting actions completed prior to another past time anchor, 
                        use the <u>Past Perfect Passive</u> (had + been + past participle).</p>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }
}
