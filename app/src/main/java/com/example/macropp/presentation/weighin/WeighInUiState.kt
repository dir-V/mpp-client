package com.example.macropp.presentation.weighin

import com.example.macropp.domain.model.WeighIn

data class WeighInUiState(
    val userId: String = "",
    val weight: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false,
    val weighInHistory: List<WeighIn> = emptyList()
)
