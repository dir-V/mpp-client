package com.example.macropp.data.remote.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class CreateWeighInRequest (
    // val user_id: UUID, // i have no idea if we need this or not, i assume we do but the CUR dto doesnt have it
    val userId: String,
    val weightKg: BigDecimal,
    val weightDate: String
)