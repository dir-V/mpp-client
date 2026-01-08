package com.example.macropp.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

data class WeighIn(
    val id: UUID,
    val userId: UUID,
    val weightKg: BigDecimal,
    val weightDate: LocalDate
     // because we use string for join date in user i'm just using this for now
)