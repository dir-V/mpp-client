package com.example.macropp.data.remote.dto

import java.math.BigDecimal
import java.util.UUID

class CreateFoodRequest (
    val userId: UUID,
    val name: String,
    val caloriesPer100Grams: Int,
    val proteinPer100Grams: BigDecimal?,
    val carbsPer100Grams: BigDecimal?,
    val fatsPer100Grams: BigDecimal?,
    val servingSizeGrams: BigDecimal?,
    val barcode: Long?
)
