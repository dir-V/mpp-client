package com.example.macropp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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

enum class AppScreen {
    Landing,
    Login,
    SignUp,
    Home,
    SetUserGoals
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MacroPPTheme {
                var currentScreen by remember { mutableStateOf(AppScreen.Landing) }

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
                            onLoginSuccess = { currentScreen = AppScreen.Home },
                            onNavigateBack = { currentScreen = AppScreen.Landing }
                        )
                    }
                    AppScreen.SignUp -> {
                        SignUpScreen(
                            onSignUpSuccess = { currentScreen = AppScreen.Home },
                            onNavigateBack = { currentScreen = AppScreen.Landing }
                        )
                    }
                    AppScreen.Home -> {
                        Text("Welcome! You are logged in.")
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
