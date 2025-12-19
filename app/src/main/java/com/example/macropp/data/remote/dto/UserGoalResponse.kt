package com.example.macropp.data.remote.dto
import java.math.BigDecimal
import java.util.UUID
import java.time.LocalDate
import java.time.LocalDateTime

data class UserGoalResponse (
    val id: UUID,
    val userId: UUID,
    val goalType: GoalType,
    val targetCalories: Int,
    val targetProteinGrams: BigDecimal,
    val targetCarbsGrams: BigDecimal,
    val targetFatsGrams: BigDecimal,
    var startDate: LocalDate?,
    var endDate: LocalDate?,
    var isActive: Boolean?,
    var createdAt: LocalDateTime
)