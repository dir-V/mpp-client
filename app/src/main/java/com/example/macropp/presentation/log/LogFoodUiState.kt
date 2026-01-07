package com.example.macropp.presentation.log

import com.example.macropp.domain.model.Food

data class LogFoodUiState(
    val searchQuery: String = "",
    val searchResults: List<Food> = emptyList(),
    val selectedFood: Food? = null,
    val quantityGrams: String = "",
    val isLoading: Boolean = false,
    val isSearching: Boolean = false,
    val error: String? = null,
    val logSuccess: Boolean = false,
    val selectedDate: String = ""
)
