package com.example.macropp.presentation.log

import com.example.macropp.domain.model.Food
import com.example.macropp.domain.model.FoodLog

data class LogFoodUiState(
    val searchQuery: String = "",
    val searchResults: List<Food> = emptyList(),
    val selectedFood: Food? = null,
    val quantityGrams: String = "",

    val scannedFoodLog: FoodLog? = null,

    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val logSuccess: Boolean = false
)