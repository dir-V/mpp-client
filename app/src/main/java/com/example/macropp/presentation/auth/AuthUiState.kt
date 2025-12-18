package com.example.macropp.presentation.auth

import com.example.macropp.domain.model.User

data class AuthUiState(
    val email: String = "",
    val height: String = "", // string for the input, we can convert later
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null // not null if we are logged in
)
