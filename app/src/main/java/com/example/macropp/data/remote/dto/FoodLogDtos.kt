package com.example.macropp.data.remote.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateFoodLogRequest(
    val userId: UUID,
    val foodId: UUID,
    val quantityGrams: BigDecimal,
    val loggedAt: String?
)

data class FoodLogResponse(
    val id: UUID,
    val userId: UUID,
    val foodId: UUID?,
    val name: String,
    val quantityGrams: BigDecimal?,
    val loggedAt: String?,
    val calories: Int,
    val proteinGrams: BigDecimal?,
    val carbsGrams: BigDecimal?,
    val fatsGrams: BigDecimal?
)

data class UpdateFoodLogTimestampRequest(
    val loggedAt: String
)
