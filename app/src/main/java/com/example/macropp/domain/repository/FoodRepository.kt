package com.example.macropp.domain.repository

import com.example.macropp.data.remote.dto.CreateFoodRequest
import com.example.macropp.domain.model.Food
import kotlinx.coroutines.flow.Flow
import java.util.UUID

interface FoodRepository {
    val foods: Flow<List<Food>>

    suspend fun createFood(createFoodRequest: CreateFoodRequest): Result<Food>

    suspend fun getFood(id: UUID): Result<Food>
}