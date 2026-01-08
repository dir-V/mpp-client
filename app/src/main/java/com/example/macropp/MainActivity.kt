package com.example.macropp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.macropp.presentation.auth.AuthViewModel
import com.example.macropp.presentation.auth.LandingScreen
import com.example.macropp.presentation.auth.LoginScreen
import com.example.macropp.presentation.auth.SignUpScreen
import com.example.macropp.presentation.food.CreateFoodScreen
import com.example.macropp.presentation.log.CameraScreen
import com.example.macropp.presentation.log.LogFoodScreen
import com.example.macropp.presentation.log.LogFoodViewModel
import com.example.macropp.presentation.log.ScanResultScreen
import com.example.macropp.presentation.navigation.MainScreen
import com.example.macropp.presentation.navigation.Routes
import com.example.macropp.presentation.userGoal.SetUserGoalsScreen
import com.example.macropp.presentation.weighin.WeighInScreen
import com.example.macropp.ui.theme.MacroPPTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.navigation.navigation

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AuthViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            MacroPPTheme {
                val navController = rememberNavController()

                val isLoading by viewModel.isLoading.collectAsState()
                val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

                if (isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                NavHost(
                    navController = navController,
                    startDestination = if (isUserLoggedIn) Routes.Main.route else Routes.Landing.route
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
                            onNavigateToLogFood = { date ->
                                navController.navigate(Routes.LogFood.createRoute(date))
                            },
                            onLogout = {
                                navController.navigate(Routes.Landing.route) {
                                    popUpTo(Routes.Main.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    navigation(
                        startDestination = Routes.LogFood.route,
                        route = Routes.LogFoodGraph.route
                    ) {
                        // 1. Log Food Screen
                        composable(Routes.LogFood.route) { backStackEntry ->
                            // This retrieves the ViewModel scoped to the GRAPH, so it survives between screens
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(Routes.LogFoodGraph.route)
                            }
                            val viewModel: LogFoodViewModel = hiltViewModel(parentEntry)

                            LogFoodScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.popBackStack() },
                                onNavigateToCreateFood = { navController.navigate(Routes.CreateFood.route) },
                                // 👇 This navigates to the next screen in the graph
                                onNavigateToCamera = { navController.navigate(Routes.Camera.route) }
                            )
                        }

                        // 2. Camera Screen
                        composable(Routes.Camera.route) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(Routes.LogFoodGraph.route)
                            }
                            val viewModel: LogFoodViewModel = hiltViewModel(parentEntry)

                            CameraScreen(
                                onNavigateBack = { navController.popBackStack() },
                                onPhotoTaken = { bitmap ->
                                    viewModel.analyzePhoto(bitmap)
                                    navController.navigate(Routes.ScanResult.route)
                                }
                            )
                        }

                        // 3. Scan Result Screen
                        composable(Routes.ScanResult.route) { backStackEntry ->
                            val parentEntry = remember(backStackEntry) {
                                navController.getBackStackEntry(Routes.LogFoodGraph.route)
                            }
                            val viewModel: LogFoodViewModel = hiltViewModel(parentEntry)

                            ScanResultScreen(
                                viewModel = viewModel,
                                onConfirm = {
                                    // Pop everything and go back to Main, or just close the graph
                                    navController.popBackStack(Routes.LogFoodGraph.route, inclusive = true)
                                },
                                onCancel = {
                                    viewModel.clearScannedData()
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                    composable(Routes.CreateFood.route) {
                        CreateFoodScreen(
                            onNavigateBack = { navController.popBackStack() }
                        )
                    }
                    composable(Routes.SetUserGoals.route) {
                        SetUserGoalsScreen(
                            onNavigateBack = { navController.popBackStack()},
                            onLogoutSuccess = {
                                navController.navigate(Routes.Landing.route){
                                    popUpTo(Routes.Main.route) { inclusive = true }
                                }
                            } ,
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
}}