package com.example.macropp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.macropp.presentation.userGoal.SetUserGoalsScreen
import com.example.macropp.ui.theme.MacroPPTheme
import dagger.hilt.android.AndroidEntryPoint

enum class AppScreen {
    Landing, Login, SignUp, Home, SetUserGoals
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel: AuthViewModel by viewModels()

        enableEdgeToEdge()
        setContent {
            MacroPPTheme {

                val isLoading by viewModel.isLoading.collectAsState()
                val isUserLoggedIn by viewModel.isUserLoggedIn.collectAsState()

                if (isLoading) {

                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {

                    val startScreen = if (isUserLoggedIn) AppScreen.SetUserGoals else AppScreen.Landing


                    var currentScreen by remember { mutableStateOf(startScreen) }

                    when (currentScreen) {
                        AppScreen.Landing -> {
                            LandingScreen(
                                onLoginClick = { currentScreen = AppScreen.Login },
                                onSignUpClick = { currentScreen = AppScreen.SignUp },
                                onSetGoalsClick = { currentScreen = AppScreen.SetUserGoals }
                            )
                        }
                        AppScreen.Login -> {
                            LoginScreen(
                                onLoginSuccess = { currentScreen = AppScreen.SetUserGoals },
                                onNavigateBack = { currentScreen = AppScreen.Landing }
                            )
                        }
                        AppScreen.SignUp -> {
                            SignUpScreen(
                                onSignUpSuccess = { currentScreen = AppScreen.SetUserGoals },
                                onNavigateBack = { currentScreen = AppScreen.Landing }
                            )
                        }
                        AppScreen.Home -> {
                            Text("Welcome! You are logged in.")
                            // Optional: Add a logout button here to test clearing session
                        }
                        AppScreen.SetUserGoals ->  {
                            SetUserGoalsScreen(
                                onNavigateBack = { currentScreen = AppScreen.Landing },
                                onSetGoalsSuccess = { currentScreen = AppScreen.Home }

                            )
                        }
                    }
                }
            }
        }
    }
}