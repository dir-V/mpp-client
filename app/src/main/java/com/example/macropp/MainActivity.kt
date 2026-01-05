package com.example.macropp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.macropp.presentation.auth.AuthScreen
import com.example.macropp.presentation.food.CreateFoodScreen
import com.example.macropp.presentation.home.HomeScreen
import com.example.macropp.presentation.navigation.Routes
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.macropp.presentation.auth.LandingScreen
import com.example.macropp.presentation.auth.LoginScreen
import com.example.macropp.presentation.auth.SignUpScreen
import com.example.macropp.presentation.userGoal.SetUserGoalsScreen
import com.example.macropp.ui.theme.MacroPPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MacroPPTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Routes.Auth.route
                ) {
                    composable(Routes.Auth.route) {
                        AuthScreen(
                            onLoginSuccess = {
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.Auth.route) { inclusive = true }
                                }
                            }
                        )
                    }
                    composable(Routes.Home.route) {
                        HomeScreen(
                            onNavigateToCreateFood = {
                                navController.navigate(Routes.CreateFood.route)
                            }
                        )
                    }
                    composable(Routes.CreateFood.route) {
                        CreateFoodScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Routes.SetUserGoals.route) {
                        SetUserGoalsScreen(
                            onNavigateBack = { navController.popBackStack() },
                            onSetGoalsSuccess = {
                                navController.navigate(Routes.Home.route) {
                                    popUpTo(Routes.Home.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
