package com.example.macropp.presentation.weighin

data class WeighInUiState(
    val userId: String = "",
    val weight: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
