package com.example.macropp.domain.model

import com.example.macropp.data.remote.dto.GoalType
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.UUID

data class UserGoal (
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