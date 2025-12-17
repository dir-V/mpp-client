package com.example.macropp.domain.model
// what BE returns after creation
data class User (
    val id: String,
    val email: String,
    val heightCm: Int,
    val joinedDate: String
)
