package com.example.macropp.domain.model

import java.util.UUID

data class WeighIn(
    val id: UUID,
    val userId: UUID,
    val weightKg: Double,
    val weightDate: String,
    val createdAt: String, // because we use string for join date in user i'm just using this for now
)