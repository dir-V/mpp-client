package com.example.macropp.presentation.userGoal
import java.util.UUID


data class GoalsUiState (
    val userId: String = "",
    val goalType: String= "",
    val targetCalories: String= "",
    val targetProteinGrams: String= "",
    val targetCarbsGrams: String= "",
    val targetFatsGrams: String= "",
    val isLoading: Boolean = false,
    val error: String? = null
)

