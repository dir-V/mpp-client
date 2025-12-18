package com.example.macropp.domain.model

import java.math.BigDecimal
import java.util.UUID

data class Food (
    val id: UUID,
    val userId: UUID,
    val name: String,
    val caloriesPer100Grams: Int,
    val proteinPer100Grams: BigDecimal?,
    val carbsPer100Grams: BigDecimal?,
    val fatsPer100Grams: BigDecimal?,
    val servingSizeGrams: BigDecimal?,
    val barcode: Long?
)