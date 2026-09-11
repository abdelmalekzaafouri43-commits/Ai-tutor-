package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val isPrimaryAction: Boolean = false
) {
    DASHBOARD("dashboard", "Dashboard", Icons.Filled.Dashboard),
    NEW_GENERATION("new_generation", "New Generation", Icons.Filled.AutoAwesome, true),
    SAVED_WORKSHEETS("saved_worksheets", "Saved Worksheets", Icons.Filled.FolderSpecial),
    ANALYTICS("analytics", "Analytics", Icons.Filled.Analytics),
    SETTINGS("settings", "Settings", Icons.Filled.Settings)
}
