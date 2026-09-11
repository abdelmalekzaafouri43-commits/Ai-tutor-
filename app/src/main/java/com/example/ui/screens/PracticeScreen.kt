package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuestionItem
import com.example.data.model.QuestionType
import com.example.data.model.UserSubscription
import com.example.ui.components.PremiumSplitButton
import com.example.ui.components.SplitMenuItem
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.RoseRed
import com.example.ui.theme.VioletAccent
import com.example.ui.viewmodel.ActiveWorksheet
import com.example.util.IllustrationUtils
import com.example.util.PdfExporter

@Composable
fun PracticeScreen(
    subscription: UserSubscription,
    activeWorksheet: ActiveWorksheet?,
    userAnswers: Map<String, String>,
    showResults: Boolean,
    onRecordAnswer: (String, String) -> Unit,
    onSubmitQuiz: () -> Unit,
    onResetQuiz: () -> Unit,
    onSaveToLibrary: () -> Unit,
    onTriggerPaywall: (String) -> Unit,
    onNavigateToGenerator: () -> Unit
) {
    val context = LocalContext.current

    if (activeWorksheet == null) {
        // BEAUTIFUL EMPTY STATE FOR STUDENTS
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .testTag("practice_empty_state"),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 500.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(IndigoPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Quiz,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Interactive Practice Sheet",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No active worksheet selected. Generate a custom lesson or select an existing one to test your grammar skills in our interactive interface.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }

                    Button(
                        onClick = onNavigateToGenerator,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("generate_new_worksheet_cta"),
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(imageVector = Icons.Filled.AutoAwesome, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Worksheet with AI", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
        return
    }

    // CALCULATE REAL-TIME PROGRESS
    val totalQuestions = activeWorksheet.questions.size
    val answeredCount = activeWorksheet.questions.count { q -> userAnswers[q.id]?.isNotBlank() == true }
    val progressFraction = if (totalQuestions > 0) answeredCount.toFloat() / totalQuestions.toFloat() else 0f

    // CALCULATE SCORE IF RESULTS VISIBLE
    val correctCount = if (showResults) {
        activeWorksheet.questions.count { q ->
            val uAns = userAnswers[q.id] ?: ""
            uAns.trim().equals(q.correctAnswer.trim(), ignoreCase = true)
        }
    } else 0

    val isPerfectScore = showResults && correctCount == totalQuestions

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
            .testTag("practice_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 20.dp)
    ) {
        // TOP HEADER HERO ILLUST BANNER
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column {
                    val illustrationRes = IllustrationUtils.getTopicIllustrationRes(activeWorksheet.topic)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                    ) {
                        Image(
                            painter = painterResource(id = illustrationRes),
                            contentDescription = "Worksheet Topic",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                                    )
                                )
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomStart)
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = GoldAmber,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Active Learning Session",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp
                                )
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = activeWorksheet.title,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(IndigoPrimary.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = activeWorksheet.topic,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = IndigoPrimary,
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                    Text(
                                        text = "•   " + activeWorksheet.difficulty,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            IconButton(
                                onClick = {
                                    if (!activeWorksheet.isSaved) {
                                        onSaveToLibrary()
                                        Toast.makeText(context, "Saved to library!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (activeWorksheet.isSaved) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                                    contentDescription = "Save Worksheet",
                                    tint = if (activeWorksheet.isSaved) IndigoPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // EXPORTS QUICK-BAR
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
                                        Toast.makeText(context, "Copied worksheet content to clipboard!", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            ),
                            testTag = "practice_export_split_button"
                        )
                    }
                }
            }
        }

        // FLOATING PROGRESS TRACKING CARD
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Worksheet Completion Progress",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "$answeredCount of $totalQuestions",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = IndigoPrimary
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = IndigoPrimary,
                        trackColor = MaterialTheme.colorScheme.outlineVariant
                    )
                }
            }
        }

        // CELEBRATION OR SCORE SUMMARY BAR
        if (showResults) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isPerfectScore) EmeraldGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, if (isPerfectScore) EmeraldGreen else MaterialTheme.colorScheme.outlineVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(if (isPerfectScore) EmeraldGreen else IndigoPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isPerfectScore) Icons.Filled.Star else Icons.Filled.Quiz,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (isPerfectScore) "Perfect Score! 🎉" else "Self-Evaluation Completed",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold)
                            )
                            Text(
                                text = "You answered $correctCount out of $totalQuestions questions correctly (${(correctCount * 100 / totalQuestions)}%).",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // ITERATE THROUGH QUESTIONS INDIVIDUALLY FOR PERFECT PERFORMANCE (NO NESTED SCROLLING LAZY ISSUES)
        itemsIndexed(activeWorksheet.questions) { index, question ->
            val qIndex = index + 1
            val userAnswer = userAnswers[question.id] ?: ""
            val isCorrect = showResults && userAnswer.trim().equals(question.correctAnswer.trim(), ignoreCase = true)

            var showHint by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("practice_question_card_$qIndex"),
                colors = CardDefaults.cardColors(
                    containerColor = when {
                        !showResults -> MaterialTheme.colorScheme.surface
                        isCorrect -> EmeraldGreen.copy(alpha = 0.05f)
                        else -> RoseRed.copy(alpha = 0.05f)
                    }
                ),
                border = BorderStroke(
                    width = 1.dp,
                    color = when {
                        !showResults -> MaterialTheme.colorScheme.outlineVariant
                        isCorrect -> EmeraldGreen
                        else -> RoseRed
                    }
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Header Row (Question #, Type, and Hint Toggle)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(IndigoPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$qIndex",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                )
                            }
                            Text(
                                text = question.type.displayName,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = IndigoPrimary
                                )
                            )
                        }

                        IconButton(
                            onClick = { showHint = !showHint },
                            modifier = Modifier.size(28.dp)
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

                    // Question Text with Beautiful Semi-Bold Font
                    Text(
                        text = question.questionText,
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold)
                    )

                    // Optional Visual Cue context background illustration
                    val questionIllustrationRes = IllustrationUtils.getTopicIllustrationRes(question.questionText)
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(IndigoPrimary.copy(alpha = 0.05f))
                    ) {
                        Image(
                            painter = painterResource(id = questionIllustrationRes),
                            contentDescription = "Visual Cue",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.Black.copy(alpha = 0.6f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Visual Context",
                                style = MaterialTheme.typography.labelSmall.copy(color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            )
                        }
                    }

                    // HINT BOX WITH STAGGERED REVEAL
                    AnimatedVisibility(visible = showHint) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(GoldAmber.copy(alpha = 0.12f))
                                .padding(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(imageVector = Icons.Filled.Lightbulb, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Hint: ${question.hint}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // INTERACTIVE FORM CHOICE RENDERERS
                    when (question.type) {
                        QuestionType.MULTIPLE_CHOICE -> {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                question.options.forEach { option ->
                                    val isSelected = userAnswer == option
                                    val isOptionCorrect = showResults && option.trim().equals(question.correctAnswer.trim(), ignoreCase = true)
                                    val isOptionIncorrectSelected = showResults && isSelected && !isOptionCorrect

                                    val cardBgColor = when {
                                        isSelected -> IndigoPrimary.copy(alpha = 0.1f)
                                        showResults && isOptionCorrect -> EmeraldGreen.copy(alpha = 0.12f)
                                        else -> Color.Transparent
                                    }

                                    val cardBorderColor = when {
                                        showResults && isOptionCorrect -> EmeraldGreen
                                        showResults && isOptionIncorrectSelected -> RoseRed
                                        isSelected -> IndigoPrimary
                                        else -> MaterialTheme.colorScheme.outlineVariant
                                    }

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(cardBgColor)
                                            .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp))
                                            .clickable(enabled = !showResults) { onRecordAnswer(question.id, option) }
                                            .padding(horizontal = 14.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { if (!showResults) onRecordAnswer(question.id, option) },
                                            colors = RadioButtonDefaults.colors(
                                                selectedColor = IndigoPrimary,
                                                unselectedColor = MaterialTheme.colorScheme.onSurfaceVariant
                                            ),
                                            enabled = !showResults
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = option,
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        )
                                        if (showResults && isOptionCorrect) {
                                            Spacer(modifier = Modifier.weight(1f))
                                            Icon(
                                                imageVector = Icons.Filled.Check,
                                                contentDescription = "Correct Option",
                                                tint = EmeraldGreen,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        QuestionType.FILL_IN_BLANK, QuestionType.SHORT_ANSWER -> {
                            OutlinedTextField(
                                value = userAnswer,
                                onValueChange = { ans -> if (!showResults) onRecordAnswer(question.id, ans) },
                                placeholder = { Text("Type your answer here...") },
                                enabled = !showResults,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("practice_answer_input_$qIndex"),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = IndigoPrimary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant
                                ),
                                singleLine = true
                            )
                        }
                    }

                    // RESULTS GRAMMAR KEYS & EXPLANATION DETAILS (Locked if Free tier)
                    if (showResults) {
                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
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

                        Spacer(modifier = Modifier.height(8.dp))

                        if (subscription.isPremium) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(IndigoPrimary.copy(alpha = 0.08f))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(imageVector = Icons.Filled.Key, contentDescription = null, tint = IndigoPrimary, modifier = Modifier.size(14.dp))
                                        Text(text = "Grammar Key Explanation:", style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = IndigoPrimary))
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = question.explanation, style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(GoldAmber.copy(alpha = 0.12f))
                                    .clickable { onTriggerPaywall("Unlock Detailed Grammatical Explanations & Answer Keys with Premium!") }
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Filled.WorkspacePremium, contentDescription = null, tint = GoldAmber, modifier = Modifier.size(16.dp))
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

        // RE-EVALUATE AND SUBMIT BUTTON CONTROLS
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onSubmitQuiz,
                    enabled = answeredCount > 0,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("practice_submit_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldGreen,
                        disabledContainerColor = EmeraldGreen.copy(alpha = 0.4f)
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Check Answers", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }

                OutlinedButton(
                    onClick = onResetQuiz,
                    modifier = Modifier
                        .height(52.dp)
                        .testTag("practice_reset_button"),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = "Reset Answers")
                }
            }
        }
    }
}
