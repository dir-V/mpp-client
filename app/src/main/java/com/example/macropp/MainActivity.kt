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
import com.example.macropp.presentation.auth.AuthScreen
import com.example.macropp.ui.theme.MacroPPTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MacroPPTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                if (isLoggedIn) {
                    Text("Welcome! You are logged in.")
                } else {
                    AuthScreen(
                        onLoginSuccess =
                            {
                                isLoggedIn = true
                            }
                    )
                }
            }
        }
    }
}
