package com.example.macropp.data.remote.dto

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class WeighInResponse (
    val id: UUID,
    val userId: UUID,
    val weightKg: BigDecimal,
    val weightDate: LocalDate
)