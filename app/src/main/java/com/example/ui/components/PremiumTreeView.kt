package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GoldAmber
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.VioletAccent

data class TreeNode(
    val id: String,
    val label: String,
    val subtitle: String? = null,
    val icon: ImageVector = Icons.Filled.Folder,
    val isPremium: Boolean = false,
    val children: List<TreeNode> = emptyList()
)

val sampleGrammarTree = listOf(
    TreeNode(
        id = "root_tenses",
        label = "Verb Tenses & Aspects",
        subtitle = "12 Core Tense Modules",
        icon = Icons.Filled.MenuBook,
        children = listOf(
            TreeNode(
                id = "tense_present",
                label = "Present Tenses",
                subtitle = "Simple, Continuous, Perfect",
                children = listOf(
                    TreeNode(id = "present_simple", label = "Present Simple & Habitual Actions"),
                    TreeNode(id = "present_continuous", label = "Present Continuous & Temporary States"),
                    TreeNode(id = "present_perfect", label = "Present Perfect vs Past Simple", isPremium = true)
                )
            ),
            TreeNode(
                id = "tense_past",
                label = "Past Tenses",
                subtitle = "Past Narrative Forms",
                children = listOf(
                    TreeNode(id = "past_simple", label = "Past Simple Regular/Irregular"),
                    TreeNode(id = "past_continuous", label = "Past Continuous Interrupted Actions"),
                    TreeNode(id = "past_perfect", label = "Past Perfect & Sequence of Events", isPremium = true)
                )
            )
        )
    ),
    TreeNode(
        id = "root_conditionals",
        label = "Conditionals & Hypotheticals",
        subtitle = "If-Clauses & Regrets",
        icon = Icons.Filled.Psychology,
        children = listOf(
            TreeNode(
                id = "cond_basic",
                label = "Zero & 1st Conditional",
                subtitle = "General truths & Real future possibilities",
                children = listOf(
                    TreeNode(id = "cond_zero", label = "Zero Conditional (If + Present)"),
                    TreeNode(id = "cond_first", label = "1st Conditional (If + Will)")
                )
            ),
            TreeNode(
                id = "cond_advanced",
                label = "2nd & 3rd Conditionals (PRO)",
                subtitle = "Unreal conditions & Past regrets",
                isPremium = true,
                children = listOf(
                    TreeNode(id = "cond_second", label = "2nd Conditional (Hypothetical Present)"),
                    TreeNode(id = "cond_third", label = "3rd Conditional (Past Regrets)", isPremium = true),
                    TreeNode(id = "cond_mixed", label = "Mixed Conditionals Complex Rules", isPremium = true)
                )
            )
        )
    ),
    TreeNode(
        id = "root_passive",
        label = "Passive Voice & Transformations",
        subtitle = "Formal Writing Shift",
        icon = Icons.Filled.AutoAwesome,
        isPremium = true,
        children = listOf(
            TreeNode(
                id = "passive_simple",
                label = "Simple Tense Passives",
                children = listOf(
                    TreeNode(id = "pass_present_simple", label = "Present Simple Passive (is/are + V3)"),
                    TreeNode(id = "pass_past_simple", label = "Past Simple Passive (was/were + V3)")
                )
            ),
            TreeNode(
                id = "passive_complex",
                label = "Agentless & Causative Passives",
                isPremium = true,
                children = listOf(
                    TreeNode(id = "pass_agentless", label = "Agentless Formal Passives", isPremium = true),
                    TreeNode(id = "pass_causative", label = "Causative Have/Get Something Done", isPremium = true)
                )
            )
        )
    )
)

@Composable
fun PremiumTreeView(
    nodes: List<TreeNode> = sampleGrammarTree,
    isUserPremium: Boolean,
    selectedNodeId: String? = null,
    onNodeSelect: (TreeNode) -> Unit,
    onTriggerPaywall: (String) -> Unit
) {
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("premium_tree_view_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(IndigoPrimary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FolderOpen,
                            contentDescription = null,
                            tint = IndigoPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Grammar Skill Tree",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = "Explore hierarchical grammar rules & topic sub-nodes",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp)),
                    color = GoldAmber.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = null,
                            tint = GoldAmber,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tree UI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tree Nodes List
            nodes.forEach { node ->
                TreeNodeItem(
                    node = node,
                    level = 0,
                    isUserPremium = isUserPremium,
                    expandedStates = expandedStates,
                    selectedNodeId = selectedNodeId,
                    onNodeSelect = onNodeSelect,
                    onTriggerPaywall = onTriggerPaywall
                )
            }
        }
    }
}

@Composable
private fun TreeNodeItem(
    node: TreeNode,
    level: Int,
    isUserPremium: Boolean,
    expandedStates: MutableMap<String, Boolean>,
    selectedNodeId: String?,
    onNodeSelect: (TreeNode) -> Unit,
    onTriggerPaywall: (String) -> Unit
) {
    val isExpanded = expandedStates[node.id] ?: (level == 0)
    val hasChildren = node.children.isNotEmpty()
    val isSelected = selectedNodeId == node.id
    val isLocked = node.isPremium && !isUserPremium

    val indentDp = (level * 18).dp

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = indentDp)
            .animateContentSize()
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 3.dp)
                .clip(RoundedCornerShape(12.dp))
                .clickable {
                    if (isLocked) {
                        onTriggerPaywall("Unlock ${node.label} in Premium Skill Tree.")
                    } else {
                        if (hasChildren) {
                            expandedStates[node.id] = !isExpanded
                        }
                        onNodeSelect(node)
                    }
                }
                .testTag("tree_node_${node.id}"),
            color = if (isSelected) {
                IndigoPrimary.copy(alpha = 0.12f)
            } else if (isLocked) {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            } else Color.Transparent,
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Connecting Branch Line Marker
                if (level > 0) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(18.dp)
                            .background(MaterialTheme.colorScheme.outlineVariant)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }

                // Expand / Collapse Chevron
                if (hasChildren) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Filled.ExpandMore else Icons.Filled.ChevronRight,
                        contentDescription = "Expand",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                } else {
                    Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outlineVariant)
                        )
                    }
                }

                // Node Icon
                Icon(
                    imageVector = node.icon,
                    contentDescription = null,
                    tint = if (isLocked) Color.Gray else if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Node Text
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = node.label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (level == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (isLocked) Color.Gray else MaterialTheme.colorScheme.onSurface
                        )
                    )
                    if (!node.subtitle.isNullOrBlank()) {
                        Text(
                            text = node.subtitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Lock Badge or Premium Crown
                if (node.isPremium) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isLocked) GoldAmber else IndigoPrimary)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isLocked) Icons.Filled.Lock else Icons.Filled.WorkspacePremium,
                                contentDescription = null,
                                tint = if (isLocked) Color.Black else Color.White,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "PRO",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 9.sp,
                                    color = if (isLocked) Color.Black else Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Child Nodes Visibility
        if (hasChildren && isExpanded) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    node.children.forEach { child ->
                        TreeNodeItem(
                            node = child,
                            level = level + 1,
                            isUserPremium = isUserPremium,
                            expandedStates = expandedStates,
                            selectedNodeId = selectedNodeId,
                            onNodeSelect = onNodeSelect,
                            onTriggerPaywall = onTriggerPaywall
                        )
                    }
                }
            }
        }
    }
}
