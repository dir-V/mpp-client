package com.example.macropp.presentation.navigation

sealed class Routes(val route: String) {
    object Auth : Routes("auth")
    object Home : Routes("home")
    object CreateFood : Routes("create_food")
    object SetUserGoals : Routes("set_user_goals")
}
