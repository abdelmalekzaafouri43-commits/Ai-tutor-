package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.WorksheetEntity
import com.example.data.model.GrammarTopic
import com.example.data.model.UserSubscription
import com.example.ui.navigation.NavItem
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletAccent

import com.example.ui.components.GlassAiTutorCard
import com.example.ui.components.PremiumSplitButton
import com.example.ui.components.PremiumTreeView
import com.example.ui.components.SplitMenuItem

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    subscription: UserSubscription,
    recentWorksheets: List<WorksheetEntity>,
    onNavigate: (NavItem) -> Unit,
    onSelectTopic: (GrammarTopic) -> Unit,
    onOpenWorksheet: (WorksheetEntity) -> Unit,
    onTriggerPaywall: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Glass AI Tutor Hub (Enlarged)
        item {
            GlassAiTutorCard(
                onTopicPromptSelected = { topic -> onSelectTopic(topic) },
                onStartGenerator = { onNavigate(NavItem.NEW_GENERATION) }
            )
        }

        // Hero Header Banner with Generated Image
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .testTag("dashboard_hero_card"),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_hero_banner_1789125014572),
                        contentDescription = "Hero Banner",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.85f),
                                        Color.Black.copy(alpha = 0.5f)
                                    )
                                )
                            )
                    )
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(IndigoPrimary)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = "AI GRAMMAR ENGINE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                            if (subscription.isPremium) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                    .background(GoldAmber)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "PRO ACTIVE",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.Black
                                        )
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Transform HTML & Topics into Interactive Worksheets",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        PremiumSplitButton(
                            mainText = "Generate New Worksheet",
                            mainIcon = Icons.Filled.AutoAwesome,
                            isPremium = subscription.isPremium,
                            containerColor = MaterialTheme.colorScheme.primary,
                            onMainClick = { onNavigate(NavItem.NEW_GENERATION) },
                            menuItems = listOf(
                                SplitMenuItem(
                                    label = "Verb Tenses Master",
                                    icon = Icons.Filled.Book,
                                    onClick = {
                                        onSelectTopic(GrammarTopic.VERB_TENSES)
                                        onNavigate(NavItem.NEW_GENERATION)
                                    }
                                ),
                                SplitMenuItem(
                                    label = "Prepositions Practice",
                                    icon = Icons.Filled.Book,
                                    onClick = {
                                        onSelectTopic(GrammarTopic.PREPOSITIONS)
                                        onNavigate(NavItem.NEW_GENERATION)
                                    }
                                ),
                                SplitMenuItem(
                                    label = "Conditionals Drills",
                                    icon = Icons.Filled.Book,
                                    onClick = {
                                        onSelectTopic(GrammarTopic.CONDITIONALS)
                                        onNavigate(NavItem.NEW_GENERATION)
                                    }
                                ),
                                SplitMenuItem(
                                    label = "Passive Voice (PRO 🔒)",
                                    icon = Icons.Filled.WorkspacePremium,
                                    isPremiumLocked = true,
                                    onClick = {
                                        if (subscription.isPremium) {
                                            onSelectTopic(GrammarTopic.PASSIVE_VOICE)
                                            onNavigate(NavItem.NEW_GENERATION)
                                        } else {
                                            onTriggerPaywall("Passive voice worksheets require Tutor PRO tier.")
                                        }
                                    }
                                )
                            ),
                            testTag = "hero_generate_split_button"
                        )
                    }
                }
            }
        }

        // Stats Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Worksheets",
                    value = recentWorksheets.size.toString(),
                    icon = Icons.Filled.Book,
                    accentColor = IndigoPrimary
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Hours Saved",
                    value = "${recentWorksheets.size * 2.5} hrs",
                    icon = Icons.Filled.Schedule,
                    accentColor = EmeraldGreen
                )
                StatCard(
                    modifier = Modifier.weight(1f),
                    title = "Tier",
                    value = if (subscription.isPremium) "Premium" else "Free",
                    icon = Icons.Filled.WorkspacePremium,
                    accentColor = GoldAmber
                )
            }
        }

        // Grammar Topics Grid Section
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Quick Start Topic Generator",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Text(
                            text = "Populate inputs and launch interactive exercises instantly",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelLarge.copy(color = IndigoPrimary),
                        modifier = Modifier.clickable { onNavigate(NavItem.NEW_GENERATION) }
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    maxItemsInEachRow = 2
                ) {
                    GrammarTopic.entries.take(6).forEach { topic ->
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onSelectTopic(topic)
                                    onNavigate(NavItem.NEW_GENERATION)
                                }
                                .testTag("topic_card_${topic.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                if (topic == GrammarTopic.HTML_CUSTOM) VioletAccent.copy(alpha = 0.15f)
                                                else IndigoPrimary.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (topic == GrammarTopic.HTML_CUSTOM) Icons.Filled.Code else Icons.Filled.AutoAwesome,
                                            contentDescription = null,
                                            tint = if (topic == GrammarTopic.HTML_CUSTOM) VioletAccent else IndigoPrimary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = topic.title,
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = topic.description,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.height(36.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Filled.Bolt,
                                            contentDescription = null,
                                            tint = GoldAmber,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        Text(
                                            text = "Instant Populate",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GoldAmber,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 9.sp
                                            )
                                        )
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(20.dp),
                                        color = IndigoPrimary.copy(alpha = 0.12f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = "Quick Start",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = IndigoPrimary,
                                                    fontWeight = FontWeight.ExtraBold,
                                                    fontSize = 9.sp
                                                )
                                            )
                                            Spacer(modifier = Modifier.width(2.dp))
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                                contentDescription = null,
                                                tint = IndigoPrimary,
                                                modifier = Modifier.size(10.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Premium Skill Tree UI Section
        item {
            PremiumTreeView(
                isUserPremium = subscription.isPremium,
                onNodeSelect = { selectedNode ->
                    onNavigate(NavItem.NEW_GENERATION)
                },
                onTriggerPaywall = onTriggerPaywall
            )
        }

        // Saved Worksheets List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved Worksheets Library",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "See All (${recentWorksheets.size})",
                    style = MaterialTheme.typography.labelLarge.copy(color = IndigoPrimary),
                    modifier = Modifier.clickable { onNavigate(NavItem.SAVED_WORKSHEETS) }
                )
            }
        }

        // Recent Worksheets
        if (recentWorksheets.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FolderSpecial,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No saved worksheets yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Generate your first AI grammar worksheet to store it here.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(recentWorksheets.take(3)) { worksheet ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenWorksheet(worksheet) }
                        .testTag("dashboard_worksheet_item_${worksheet.id}"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = worksheet.title,
                                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(IndigoPrimary.copy(alpha = 0.1f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = worksheet.topic,
                                        style = MaterialTheme.typography.labelSmall.copy(color = IndigoPrimary)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = worksheet.difficulty,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Icon(
                            imageVector = Icons.Filled.Book,
                            contentDescription = "Open",
                            tint = IndigoPrimary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    accentColor: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(accentColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
