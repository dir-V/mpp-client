package com.example.macropp.data.remote.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateGeminiRequest(
    val userId: UUID,
    val name: String = "AI Scan", // Default name
    val calories: Int,
    val proteinGrams: BigDecimal,
    val carbsGrams: BigDecimal,
    val fatsGrams: BigDecimal,
    val loggedAt: String?
)