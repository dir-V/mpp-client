package com.example.macropp.domain.model

import java.math.BigDecimal
import java.util.UUID

data class FoodLog(
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
