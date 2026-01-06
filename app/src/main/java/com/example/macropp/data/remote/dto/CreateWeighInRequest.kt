package com.example.macropp.data.remote.dto

import java.util.UUID

data class CreateWeighInRequest (
    // val user_id: UUID, // i have no idea if we need this or not, i assume we do but the CUR dto doesnt have it
    val weight_kg: Double,
    val weight_date: String // just doing string for now like the last time
)