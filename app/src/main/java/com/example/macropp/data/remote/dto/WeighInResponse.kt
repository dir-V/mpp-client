package com.example.macropp.data.remote.dto

import java.util.UUID

data class WeighInResponse (
    // val user_id: UUID,
    val weight_kg: Double,
    val weight_date: String
)