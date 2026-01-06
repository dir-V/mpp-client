package com.example.macropp.presentation.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.macropp.presentation.home.HomeScreen
import com.example.macropp.presentation.userGoal.SetUserGoalsScreen
import com.example.macropp.presentation.weight.WeightScreen

@Composable
fun MainScreen(
    onNavigateToLogFood: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                currentRoute = currentRoute,
                onItemClick = { item ->
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        },
        modifier = modifier
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Home.route,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.Weight.route) {
                WeightScreen()
            }
            composable(Routes.Home.route) {
                HomeScreen(
                    onNavigateToLogFood = onNavigateToLogFood
                )
            }
            composable(Routes.Goals.route) {
                SetUserGoalsScreen(
                    onNavigateBack = { },
                    onSetGoalsSuccess = { }
                )
            }
        }
    }
}
