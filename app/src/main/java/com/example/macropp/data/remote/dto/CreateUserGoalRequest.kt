package com.example.macropp.data.remote.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateUserGoalRequest(
    val userId: UUID,
    val goalType: GoalType,
    val targetCalories: Int,
    val targetProteinGrams: BigDecimal,
    val targetCarbsGrams: BigDecimal,
    val targetFatsGrams: BigDecimal
)
