package com.example.macropp.presentation.weighin

data class WeighInUiState(
    val weight: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)
