package com.example.macropp.domain.model

import java.util.UUID

// what BE returns after creation
data class User (
    val id: UUID,
    val email: String,
    val heightCm: Int,
    val joinedDate: String
)
