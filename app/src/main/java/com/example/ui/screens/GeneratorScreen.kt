/*
 * GeneratorScreen.kt
 * Displays the AI Worksheet Tutor generation control panel with adjustable settings.
 * Includes layout structure, grade levels, topics, output structure selection, and Gemini generation triggering.
 */
package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DifficultyLevel
import com.example.data.model.GrammarTopic
import com.example.data.model.OutputStructure
import com.example.data.model.QuestionItem
import com.example.data.model.QuestionType
import com.example.data.model.UserSubscription
import com.example.ui.components.PremiumCascadingSelector
import com.example.ui.components.PremiumSplitButton
import com.example.ui.components.SplitMenuItem
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseRed
import com.example.ui.theme.Slate800
import com.example.ui.theme.VioletAccent
import com.example.ui.viewmodel.ActiveWorksheet
import com.example.util.IllustrationUtils
import com.example.util.PdfExporter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GeneratorScreen(
    subscription: UserSubscription,
    inputMode: String, // "TOPIC" or "HTML"
    rawHtmlInput: String,
    selectedTopic: GrammarTopic,
    selectedDifficulty: DifficultyLevel,
    includeExplanations: Boolean,
    specificRuleFilter: String,
    outputStructure: OutputStructure,
    isGenerating: Boolean,
    generationProgressMessage: String,
    activeWorksheet: ActiveWorksheet?,
    userAnswers: Map<String, String>,
    showResults: Boolean,
    onSetInputMode: (String) -> Unit,
    onSetRawHtmlInput: (String) -> Unit,
    onSetSelectedTopic: (GrammarTopic) -> Unit,
    onSetSelectedDifficulty: (DifficultyLevel) -> Unit,
    onSetIncludeExplanations: (Boolean) -> Unit,
    onSetSpecificRuleFilter: (String) -> Unit,
    onSetOutputStructure: (OutputStructure) -> Unit,
    onLoadSampleHtml: () -> Unit,
    onStartGeneration: () -> Unit,
    onSaveToLibrary: () -> Unit,
    onRecordAnswer: (String, String) -> Unit,
    onSubmitQuiz: () -> Unit,
    onResetQuiz: () -> Unit,
    onTriggerPaywall: (String) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("generator_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Mode Selector Header (Topic vs HTML Input) with Frosted Glass AI Tutor finish
        item {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .border(
                        border = BorderStroke(
                            width = 1.5.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0.7f),
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                                    Color.White.copy(alpha = 0.3f)
                                )
                            )
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f),
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                                    MaterialTheme.colorScheme.surface.copy(alpha = 0.90f)
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Filled.AutoAwesome,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "AI GRAMMAR TUTOR GENERATOR",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.ExtraBold,
                                            letterSpacing = 0.5.sp
                                        )
                                    )
                                    Text(
                                        text = "Powered by Gemini 3.5 Flash Model",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        TabRow(
                            selectedTabIndex = if (inputMode == "TOPIC") 0 else 1,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            indicator = { tabPositions ->
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[if (inputMode == "TOPIC") 0 else 1]),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.clip(RoundedCornerShape(12.dp))
                        ) {
                        Tab(
                            selected = inputMode == "TOPIC",
                            onClick = { onSetInputMode("TOPIC") },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Filled.Psychology, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Grammar Topic")
                                }
                            },
                            selectedContentColor = IndigoPrimary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Tab(
                            selected = inputMode == "HTML",
                            onClick = { onSetInputMode("HTML") },
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(imageVector = Icons.Filled.Code, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Paste HTML Code")
                                }
                            },
                            selectedContentColor = IndigoPrimary,
                            unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (inputMode == "TOPIC") {
                        Column {
                            // Topic Dropdown Selection
                            var topicExpanded by remember { mutableStateOf(false) }

                            ExposedDropdownMenuBox(
                                expanded = topicExpanded,
                                onExpandedChange = { topicExpanded = !topicExpanded },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = selectedTopic.title,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Select Focus Grammar Topic") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = topicExpanded) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth()
                                        .testTag("topic_dropdown"),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = IndigoPrimary
                                    )
                                )
                                ExposedDropdownMenu(
                                    expanded = topicExpanded,
                                    onDismissRequest = { topicExpanded = false }
                                ) {
                                    GrammarTopic.entries.forEach { topic ->
                                        DropdownMenuItem(
                                            text = {
                                                Column {
                                                    Text(topic.title, fontWeight = FontWeight.Bold)
                                                    Text(topic.description, fontSize = 12.sp, color = Color.Gray)
                                                }
                                            },
                                            onClick = {
                                                onSetSelectedTopic(topic)
                                                topicExpanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Raw Text or HTML Code Input Area
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "HTML Code Input Area",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                OutlinedButton(
                                    onClick = onLoadSampleHtml,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.testTag("load_sample_html_button")
                                ) {
                                    Text("Load Sample HTML", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = rawHtmlInput,
                                onValueChange = onSetRawHtmlInput,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .testTag("html_code_input"),
                                placeholder = { Text("Paste raw HTML code markup here...", fontFamily = FontFamily.Monospace) },
                                shape = RoundedCornerShape(12.dp),
                                textStyle = MaterialTheme.typography.bodySmall.copy(fontFamily = FontFamily.Monospace),
                                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = IndigoPrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Advanced Customization Options
                    Text(
                        text = "Worksheet Customization",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Difficulty selector
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        DifficultyLevel.entries.forEach { diff ->
                            val isSelected = selectedDifficulty == diff
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .clickable { onSetSelectedDifficulty(diff) }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = diff.displayName.split(" ")[0],
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Selection block for the output worksheet structure (Exercises, Explanations, or Quiz)
                    Text(
                        text = "Output Worksheet Structure",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutputStructure.entries.forEach { structure ->
                            val isSelected = outputStructure == structure
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                                        } else {
                                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                                        }
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                                        shape = RoundedCornerShape(12.dp)
                                    )
                                    .clickable { onSetOutputStructure(structure) }
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = { onSetOutputStructure(structure) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = IndigoPrimary
                                    ),
                                    modifier = Modifier.testTag("output_structure_${structure.name.lowercase()}")
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = structure.displayName,
                                        style = MaterialTheme.typography.bodyMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Text(
                                        text = structure.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Specific Grammar Filter input
                    OutlinedTextField(
                        value = specificRuleFilter,
                        onValueChange = onSetSpecificRuleFilter,
                        label = { Text("Specific Grammar Focus Filter") },
                        placeholder = { Text("e.g. Focus on passive voice transformations") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("grammar_rule_filter_input"),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Detailed Answer Key & Explanations Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(if (includeExplanations) IndigoPrimary else Color.Gray),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Filled.Key, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Detailed Answer Keys & Explanations",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    if (!subscription.isPremium) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Filled.WorkspacePremium,
                                            contentDescription = "Premium Feature",
                                            tint = GoldAmber,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Generates grammatical justifications for each question.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = includeExplanations,
                            onCheckedChange = { onSetIncludeExplanations(it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = IndigoPrimary
                            ),
                            modifier = Modifier.testTag("explanations_toggle_switch")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Cascading Parameter Selector UI
                    PremiumCascadingSelector(
                        selectedTopic = selectedTopic,
                        selectedDifficulty = selectedDifficulty,
                        includeExplanations = includeExplanations,
                        isUserPremium = subscription.isPremium,
                        onSelectTopic = { onSetSelectedTopic(it) },
                        onSelectDifficulty = { onSetSelectedDifficulty(it) },
                        onToggleExplanations = { onSetIncludeExplanations(it) },
                        onTriggerPaywall = onTriggerPaywall
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Generate Primary Action Split Button
                    PremiumSplitButton(
                        mainText = if (isGenerating) "Generating Worksheet..." else "Generate AI Grammar Worksheet",
                        mainIcon = Icons.Filled.AutoAwesome,
                        isPremium = subscription.isPremium,
                        containerColor = MaterialTheme.colorScheme.primary,
                        onMainClick = { if (!isGenerating) onStartGeneration() },
                        menuItems = listOf(
                            SplitMenuItem(
                                label = "Standard Topic Generation",
                                icon = Icons.Filled.Lightbulb,
                                onClick = {
                                    onSetInputMode("TOPIC")
                                    onStartGeneration()
                                }
                            ),
                            SplitMenuItem(
                                label = "HTML Code Analysis",
                                icon = Icons.Filled.Code,
                                onClick = {
                                    onSetInputMode("HTML")
                                    onLoadSampleHtml()
                                }
                            ),
                            SplitMenuItem(
                                label = "Batch PRO Generator (🔒)",
                                icon = Icons.Filled.WorkspacePremium,
                                isPremiumLocked = true,
                                onClick = {
                                    if (subscription.isPremium) {
                                        onStartGeneration()
                                    } else {
                                        onTriggerPaywall("Batch worksheet generation is a Tutor PRO feature.")
                                    }
                                }
                            )
                        ),
                        testTag = "generate_worksheet_split_button"
                    )
                }
            }
        }
    }

        // Processing Loading State Animation Box
        if (isGenerating) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val infiniteTransition = rememberInfiniteTransition(label = "rotation")
                        val rotation by infiniteTransition.animateFloat(
                            initialValue = 0f,
                            targetValue = 360f,
                            animationSpec = infiniteRepeatable(animation = tween(1200, easing = LinearEasing)),
                            label = "spin"
                        )

                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = "Generating",
                            tint = IndigoPrimary,
                            modifier = Modifier
                                .size(48.dp)
                                .rotate(rotation)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "AI Grammar Engine Processing...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = generationProgressMessage,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Generated Output Screen & Live Worksheet Preview
        if (activeWorksheet != null && !isGenerating) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("worksheet_output_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(20.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        // Topic Illustration Banner Picture
                        val illustrationRes = IllustrationUtils.getTopicIllustrationRes(activeWorksheet.topic)
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(IndigoPrimary.copy(alpha = 0.1f))
                        ) {
                            Image(
                                painter = painterResource(id = illustrationRes),
                                contentDescription = "Worksheet Topic Illustration",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                            Surface(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(8.dp)
                                    .clip(RoundedCornerShape(6.dp)),
                                color = Color.Black.copy(alpha = 0.65f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.AutoAwesome,
                                        contentDescription = null,
                                        tint = GoldAmber,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Visual Context Illustration",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Header Toolbar (School Name, Title, Export Actions)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = subscription.schoolName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = IndigoPrimary,
                                        letterSpacing = 1.sp
                                    )
                                )
                                Text(
                                    text = activeWorksheet.title,
                                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(IndigoPrimary.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = activeWorksheet.topic, style = MaterialTheme.typography.labelSmall.copy(color = IndigoPrimary))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = activeWorksheet.difficulty,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            // Save to Library Button
                            val context = LocalContext.current
                            IconButton(
                                onClick = {
                                    if (!activeWorksheet.isSaved) {
                                        onSaveToLibrary()
                                        Toast.makeText(context, "Saved successfully to Room Local Storage!", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                modifier = Modifier.testTag("save_library_button")
                            ) {
                                Icon(
                                    imageVector = if (activeWorksheet.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Save to Library",
                                    tint = if (activeWorksheet.isSaved) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Export Actions Row using PremiumSplitButton
                        PremiumSplitButton(
                            mainText = "Export PDF Worksheet",
                            mainIcon = Icons.Filled.Download,
                            isPremium = subscription.isPremium,
                            isLockedForFree = false,
                            containerColor = MaterialTheme.colorScheme.secondary,
                            onMainClick = {
                                PdfExporter.generateAndSharePdf(context, activeWorksheet, subscription.schoolName)
                            },
                            menuItems = listOf(
                                SplitMenuItem(
                                    label = "Print PDF Worksheet",
                                    icon = Icons.Filled.Download,
                                    isPremiumLocked = false,
                                    onClick = {
                                        PdfExporter.generateAndPrintPdf(context, activeWorksheet, subscription.schoolName)
                                    }
                                ),
                                SplitMenuItem(
                                    label = "Export to Google Docs",
                                    icon = Icons.Filled.Description,
                                    isPremiumLocked = false,
                                    onClick = {
                                        PdfExporter.generateAndSharePdf(context, activeWorksheet, subscription.schoolName)
                                    }
                                ),
                                SplitMenuItem(
                                    label = "Copy Raw Worksheet Text",
                                    icon = Icons.Filled.Code,
                                    onClick = {
                                        Toast.makeText(context, "Worksheet raw text copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            ),
                            testTag = "export_split_button"
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Interactive Questions List
                        Text(
                            text = "Interactive Student Practice Sheet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Answer the questions below to validate your understanding.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        activeWorksheet.questions.forEachIndexed { index, question ->
                            QuestionCardItem(
                                questionIndex = index + 1,
                                question = question,
                                isPremium = subscription.isPremium,
                                userAnswer = userAnswers[question.id] ?: "",
                                showResults = showResults,
                                onAnswerSelected = { answer -> onRecordAnswer(question.id, answer) },
                                onTriggerPaywall = onTriggerPaywall
                            )
                            Spacer(modifier = Modifier.height(14.dp))
                        }

                        // Submit Quiz / Reset Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = onSubmitQuiz,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("submit_quiz_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Check Answers", fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = onResetQuiz,
                                modifier = Modifier.height(48.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Reset")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuestionCardItem(
    questionIndex: Int,
    question: QuestionItem,
    isPremium: Boolean,
    userAnswer: String,
    showResults: Boolean,
    onAnswerSelected: (String) -> Unit,
    onTriggerPaywall: (String) -> Unit
) {
    var showHint by remember { mutableStateOf(false) }

    val isCorrect = showResults && userAnswer.trim().equals(question.correctAnswer.trim(), ignoreCase = true)
    val isAnswered = userAnswer.isNotBlank()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("question_item_$questionIndex"),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !showResults -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                isCorrect -> EmeraldGreen.copy(alpha = 0.1f)
                else -> RoseRed.copy(alpha = 0.1f)
            }
        ),
        border = if (showResults) {
            androidx.compose.foundation.BorderStroke(1.dp, if (isCorrect) EmeraldGreen else RoseRed)
        } else null,
        shape = RoundedCornerShape(14.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row (Question Number + Type Badge + Hint Toggle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$questionIndex",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = question.type.displayName,
                        style = MaterialTheme.typography.labelSmall.copy(color = IndigoPrimary, fontWeight = FontWeight.Bold)
                    )
                }

                IconButton(
                    onClick = { showHint = !showHint },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Lightbulb,
                        contentDescription = "Hint",
                        tint = if (showHint) GoldAmber else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Visual Illustration Context Card
            val qIllustrationRes = IllustrationUtils.getTopicIllustrationRes(question.questionText)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(IndigoPrimary.copy(alpha = 0.08f))
            ) {
                Image(
                    painter = painterResource(id = qIllustrationRes),
                    contentDescription = "Question Visual Picture Context",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = Color.Black.copy(alpha = 0.6f)
                ) {
                    Text(
                        text = "Visual Cue",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Question Text
            Text(
                text = question.questionText,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
            )

            // Hint Box
            AnimatedVisibility(visible = showHint) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(GoldAmber.copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Hint: ${question.hint}", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Options or Input depending on QuestionType
            when (question.type) {
                QuestionType.MULTIPLE_CHOICE -> {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        question.options.forEach { option ->
                            val selected = userAnswer == option
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        if (selected) IndigoPrimary.copy(alpha = 0.15f) else Color.Transparent
                                    )
                                    .clickable { onAnswerSelected(option) }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = selected,
                                    onClick = { onAnswerSelected(option) },
                                    colors = RadioButtonDefaults.colors(selectedColor = IndigoPrimary)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = option, style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                    }
                }

                QuestionType.FILL_IN_BLANK, QuestionType.SHORT_ANSWER -> {
                    OutlinedTextField(
                        value = userAnswer,
                        onValueChange = onAnswerSelected,
                        placeholder = { Text("Type your answer here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("answer_input_$questionIndex"),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }

            // Results Explanation View (Locked if Free tier)
            if (showResults) {
                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(10.dp))

                if (isCorrect) {
                    Text(
                        text = "✓ Correct Answer!",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    )
                } else {
                    Text(
                        text = "✗ Incorrect. Correct: ${question.correctAnswer}",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = RoseRed)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Detailed Explanation Key (Locked if Free)
                if (isPremium) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(IndigoPrimary.copy(alpha = 0.08f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(text = "Explanation Key:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = IndigoPrimary))
                            Text(text = question.explanation, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(GoldAmber.copy(alpha = 0.12f))
                            .clickable { onTriggerPaywall("Unlock Detailed Grammatical Explanations & Answer Keys with Premium!") }
                            .padding(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.WorkspacePremium, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "🔒 Unlock Detailed Answer Explanation Key (Premium)",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = Color.Black)
                            )
                        }
                    }
                }
            }
        }
    }
}
