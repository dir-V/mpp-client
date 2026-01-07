package com.example.macropp.presentation.home

import com.example.macropp.domain.model.FoodLog
import java.math.BigDecimal
import java.time.LocalDate

data class HomeUiState(
    val foodLogs: List<FoodLog> = emptyList(),
    val totalCalories: Int = 0,
    val totalProtein: BigDecimal = BigDecimal.ZERO,
    val totalCarbs: BigDecimal = BigDecimal.ZERO,
    val totalFats: BigDecimal = BigDecimal.ZERO,
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFoodLogForEdit: FoodLog? = null,
    val isUpdatingTimestamp: Boolean = false,
    val isDeletingFoodLog: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now()
)
