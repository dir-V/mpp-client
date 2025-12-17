package com.example.macropp.data.remote.dto

import java.util.UUID

data class UserResponse (
    val id: UUID,
    val email: String,
    val heightCm: Int,
    val joinedDate: String
)
