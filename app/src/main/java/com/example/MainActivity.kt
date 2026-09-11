package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.PaywallModal
import com.example.ui.components.SidebarDashboardLayout
import com.example.ui.navigation.NavItem
import com.example.ui.screens.AnalyticsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.GeneratorScreen
import com.example.ui.screens.SavedWorksheetsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AIWorksheetTutorTheme
import com.example.ui.viewmodel.WorksheetViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: WorksheetViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val selectedTheme by viewModel.selectedTheme.collectAsStateWithLifecycle()

            AIWorksheetTutorTheme(appTheme = selectedTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val currentNavItem by viewModel.currentNavItem.collectAsStateWithLifecycle()
                    val isSidebarExpanded by viewModel.isSidebarExpanded.collectAsStateWithLifecycle()
                    val subscription by viewModel.subscription.collectAsStateWithLifecycle()
                    val showPaywallModal by viewModel.showPaywallModal.collectAsStateWithLifecycle()
                    val paywallReason by viewModel.paywallReason.collectAsStateWithLifecycle()

                    val inputMode by viewModel.inputMode.collectAsStateWithLifecycle()
                    val rawHtmlInput by viewModel.rawHtmlInput.collectAsStateWithLifecycle()
                    val selectedTopic by viewModel.selectedTopic.collectAsStateWithLifecycle()
                    val selectedDifficulty by viewModel.selectedDifficulty.collectAsStateWithLifecycle()
                    val includeExplanations by viewModel.includeExplanations.collectAsStateWithLifecycle()
                    val specificRuleFilter by viewModel.specificRuleFilter.collectAsStateWithLifecycle()
                    val outputStructure by viewModel.outputStructure.collectAsStateWithLifecycle()

                    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
                    val generationProgressMessage by viewModel.generationProgressMessage.collectAsStateWithLifecycle()
                    val activeWorksheet by viewModel.activeWorksheet.collectAsStateWithLifecycle()

                    val userAnswers by viewModel.userAnswers.collectAsStateWithLifecycle()
                    val showResults by viewModel.showResults.collectAsStateWithLifecycle()

                    val savedWorksheets by viewModel.savedWorksheets.collectAsStateWithLifecycle()

                    SidebarDashboardLayout(
                        currentNavItem = currentNavItem,
                        isExpanded = isSidebarExpanded,
                        subscription = subscription,
                        onToggleSidebar = { viewModel.toggleSidebar() },
                        onNavigate = { item -> viewModel.navigateTo(item) },
                        onTriggerPaywall = { reason -> viewModel.triggerPaywall(reason) },
                        onToggleTierDev = { viewModel.toggleSubscriptionTier() }
                    ) {
                        when (currentNavItem) {
                            NavItem.DASHBOARD -> DashboardScreen(
                                subscription = subscription,
                                recentWorksheets = savedWorksheets,
                                onNavigate = { item -> viewModel.navigateTo(item) },
                                onSelectTopic = { topic -> viewModel.setSelectedTopic(topic) },
                                onOpenWorksheet = { entity -> viewModel.loadSavedWorksheet(entity) },
                                onTriggerPaywall = { reason -> viewModel.triggerPaywall(reason) }
                            )

                            NavItem.NEW_GENERATION -> GeneratorScreen(
                                subscription = subscription,
                                inputMode = inputMode,
                                rawHtmlInput = rawHtmlInput,
                                selectedTopic = selectedTopic,
                                selectedDifficulty = selectedDifficulty,
                                includeExplanations = includeExplanations,
                                specificRuleFilter = specificRuleFilter,
                                outputStructure = outputStructure,
                                isGenerating = isGenerating,
                                generationProgressMessage = generationProgressMessage,
                                activeWorksheet = activeWorksheet,
                                userAnswers = userAnswers,
                                showResults = showResults,
                                onSetInputMode = { mode -> viewModel.setInputMode(mode) },
                                onSetRawHtmlInput = { text -> viewModel.setRawHtmlInput(text) },
                                onSetSelectedTopic = { topic -> viewModel.setSelectedTopic(topic) },
                                onSetSelectedDifficulty = { diff -> viewModel.setSelectedDifficulty(diff) },
                                onSetIncludeExplanations = { include -> viewModel.setIncludeExplanations(include) },
                                onSetSpecificRuleFilter = { filter -> viewModel.setSpecificRuleFilter(filter) },
                                onSetOutputStructure = { structure -> viewModel.setOutputStructure(structure) },
                                onLoadSampleHtml = { viewModel.loadSampleHtml() },
                                onStartGeneration = { viewModel.startNewGeneration() },
                                onSaveToLibrary = { viewModel.saveCurrentWorksheetToLibrary() },
                                onRecordAnswer = { qId, ans -> viewModel.recordAnswer(qId, ans) },
                                onSubmitQuiz = { viewModel.submitQuiz() },
                                onResetQuiz = { viewModel.resetQuiz() },
                                onTriggerPaywall = { reason -> viewModel.triggerPaywall(reason) }
                            )

                            NavItem.SAVED_WORKSHEETS -> SavedWorksheetsScreen(
                                subscription = subscription,
                                savedWorksheets = savedWorksheets,
                                onOpenWorksheet = { entity -> viewModel.loadSavedWorksheet(entity) },
                                onDeleteWorksheet = { id -> viewModel.deleteWorksheet(id) },
                                onToggleFavorite = { id, fav -> viewModel.toggleFavorite(id, fav) },
                                onParseQuestions = { json -> viewModel.parseQuestions(json) },
                                onTriggerPaywall = { reason -> viewModel.triggerPaywall(reason) }
                            )

                            NavItem.ANALYTICS -> AnalyticsScreen(
                                savedWorksheets = savedWorksheets
                            )

                            NavItem.SETTINGS -> SettingsScreen(
                                subscription = subscription,
                                selectedTheme = selectedTheme,
                                onSelectTheme = { theme -> viewModel.setSelectedTheme(theme) },
                                onToggleTierDev = { viewModel.toggleSubscriptionTier() },
                                onTriggerPaywall = { reason -> viewModel.triggerPaywall(reason) }
                            )
                        }
                    }

                    // Paywall Modal Dialog
                    PaywallModal(
                        isVisible = showPaywallModal,
                        reason = paywallReason,
                        onDismiss = { viewModel.dismissPaywall() },
                        onUpgradeSuccess = { viewModel.toggleSubscriptionTier() }
                    )
                }
            }
        }
    }
}
