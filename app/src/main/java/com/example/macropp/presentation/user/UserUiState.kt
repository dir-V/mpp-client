package com.example.macropp.presentation.user

import com.example.macropp.domain.model.User

data class UserUiState (
    val user: User? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)