package com.example.macropp.data.remote.dto

import java.math.BigDecimal
import java.util.UUID
//TODO: check if type should be string
data class CreateUserGoalRequest(
    val userId: String,
    val goalType: GoalType,
    val targetCalories: Int,
    val targetProteinGrams: BigDecimal,
    val targetCarbsGrams: BigDecimal,
    val targetFatsGrams: BigDecimal
)
