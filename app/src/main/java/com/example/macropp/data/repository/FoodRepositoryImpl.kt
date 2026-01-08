package com.example.macropp.data.repository

import com.example.macropp.data.remote.FoodApi
import com.example.macropp.data.remote.dto.CreateFoodRequest
import com.example.macropp.data.remote.dto.CreateGeminiRequest
import com.example.macropp.data.remote.dto.FoodResponse
import com.example.macropp.domain.model.Food
import com.example.macropp.domain.repository.FoodRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class FoodRepositoryImpl (
    private val api: FoodApi
) : FoodRepository {
    private val _foods = MutableStateFlow<List<Food>>(emptyList())
    override val foods: Flow<List<Food>> = _foods.asStateFlow()

    override suspend fun createFood(createFoodRequest: CreateFoodRequest): Result<Food> {
        return try {
            val requestDto = createFoodRequest.toRequestDto()
            val responseDto = api.createFood(requestDto)
            val food = responseDto.toDomain()

            _foods.update { currentList -> currentList + food }

            Result.success(food)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getFood(id: UUID): Result<Food> {
        return try {
            val responseDto = api.getFood(id.toString())
            val food = responseDto.toDomain()

            Result.success(food)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun searchFoods(userId: UUID, query: String): Result<List<Food>> {
        return try {
            val response = api.searchUserFoods(userId.toString(), query)
            val foods = response.map { it.toDomain() }

            Result.success(foods)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

    // Mappers
private fun CreateFoodRequest.toRequestDto(): CreateFoodRequest {
    return CreateFoodRequest(
        userId = userId,
        name = name,
        caloriesPer100Grams = caloriesPer100Grams,
        proteinPer100Grams = proteinPer100Grams,
        carbsPer100Grams = carbsPer100Grams,
        fatsPer100Grams = fatsPer100Grams,
        servingSizeGrams = servingSizeGrams,
        barcode = barcode
    )
}

private fun FoodResponse.toDomain(): Food {
    return Food(
        id = id,
        userId = userId,
        name = name,
        caloriesPer100Grams = caloriesPer100Grams,
        proteinPer100Grams = proteinPer100Grams,
        carbsPer100Grams = carbsPer100Grams,
        fatsPer100Grams = fatsPer100Grams,
        servingSizeGrams = servingSizeGrams,
        barcode = barcode
    )
}