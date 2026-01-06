package com.example.macropp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.activity.viewModels // Ensure this import is here
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.macropp.presentation.auth.AuthViewModel
import com.example.macropp.presentation.auth.LandingScreen
import com.example.macropp.presentation.auth.LoginScreen
import com.example.macropp.presentation.auth.SignUpScreen
import com.example.macropp.presentation.food.CreateFoodScreen
import com.example.macropp.presentation.log.LogFoodScreen
import com.example.macropp.presentation.navigation.MainScreen
import com.example.macropp.presentation.navigation.Routes
import com.example.macropp.presentation.userGoal.SetUserGoalsScreen
import com.example.macropp.ui.theme.MacroPPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AuthViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            MacroPPTheme {
                val navController = rememberNavController()

//                val isLoading by viewModel.isLoading.collectAsState()
//                val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()
//
//                if (isLoading) {
//
//                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                        CircularProgressIndicator()
//                    }
//                } else {
//
//                    val startScreen = if (isUserLoggedIn) AppScreen.SetUserGoals else AppScreen.Landing
//
//
//                    var currentScreen by remember { mutableStateOf(startScreen) }

                NavHost(
                    navController = navController,
                    startDestination = Routes.Landing.route
                ) {
                    composable(Routes.Landing.route) {
                        LandingScreen(
                            onLoginClick = { navController.navigate(Routes.Login.route) },
                            onSignUpClick = { navController.navigate(Routes.SignUp.route) },
                            onSetGoalsClick = { navController.navigate(Routes.SetUserGoals.route) }
                        )
                    }
                    composable(Routes.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                navController.navigate(Routes.Main.route) {
                                    popUpTo(Routes.Landing.route) { inclusive = true }
                                }
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Routes.SignUp.route) {
                        SignUpScreen(
                            onSignUpSuccess = {
                                navController.navigate(Routes.Main.route) {
                                    popUpTo(Routes.Landing.route) { inclusive = true }
                                }
                            },
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Routes.Main.route) {
                        MainScreen(
                            onNavigateToLogFood = {
                                navController.navigate(Routes.LogFood.route)
                            }
                        )
                    }
                    composable(Routes.LogFood.route) {
                        LogFoodScreen(
                            onNavigateBack = { navController.popBackStack() },
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
                                navController.navigate(Routes.Main.route) {
                                    popUpTo(Routes.Landing.route) { inclusive = true }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
