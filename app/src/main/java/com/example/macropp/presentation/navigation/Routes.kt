package com.example.macropp.presentation.navigation

sealed class Routes(val route: String) {
    object Landing : Routes("landing")
    object Login : Routes("login")
    object SignUp : Routes("sign_up")
    object Main : Routes("main")
    object Home : Routes("home")
    object Weight : Routes("weight")
    object Goals : Routes("goals")
    object CreateFood : Routes("create_food")
    object LogFood : Routes("log_food/{date}") {
        fun createRoute(date: String) = "log_food/$date"
    }
    object SetUserGoals : Routes("set_user_goals")
}
