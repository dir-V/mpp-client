package com.example.macropp.presentation.navigation

sealed class Routes(val route: String) {
    object Landing : Routes("landing")
    object Login : Routes("login")
    object SignUp : Routes("sign_up")
    object Home : Routes("home")
    object CreateFood : Routes("create_food")
    object SetUserGoals : Routes("set_user_goals")
}
