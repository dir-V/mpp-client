package com.example.macropp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
) {
    object Weight : BottomNavItem(
        route = Routes.Weight.route,
        icon = Icons.Default.MonitorWeight,
        label = "Weight"
    )

    object FoodLog : BottomNavItem(
        route = Routes.Home.route,
        icon = Icons.Default.Restaurant,
        label = "Food Log"
    )

    object Goals : BottomNavItem(
        route = Routes.Goals.route,
        icon = Icons.Default.Flag,
        label = "Goals"
    )

    companion object {
        val items = listOf(Weight, FoodLog, Goals)
    }
}
