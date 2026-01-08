package com.example.macropp.data.remote.dto

import java.math.BigDecimal
import java.util.UUID

data class CreateGeminiRequest(
    val userId: UUID,

    // 👇 CHANGED: "name" -> "quickName" to match the Backend DTO
    val name: String = "AI Scan",

    val calories: Int,

    // These already match your backend DTO perfectly:
    val proteinGrams: BigDecimal,
    val carbsGrams: BigDecimal,
    val fatsGrams: BigDecimal,

    val loggedAt: String?
)