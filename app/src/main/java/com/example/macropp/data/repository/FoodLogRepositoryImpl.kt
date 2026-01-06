package com.example.macropp.data.repository

import com.example.macropp.data.remote.FoodLogApi
import com.example.macropp.data.remote.dto.CreateFoodLogRequest
import com.example.macropp.data.remote.dto.FoodLogResponse
import com.example.macropp.data.remote.dto.UpdateFoodLogTimestampRequest
import com.example.macropp.domain.model.FoodLog
import com.example.macropp.domain.repository.FoodLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.math.BigDecimal
import java.util.UUID

class FoodLogRepositoryImpl(
    private val api: FoodLogApi
) : FoodLogRepository {
    private val _foodLogs = MutableStateFlow<List<FoodLog>>(emptyList())
    override val foodLogs: Flow<List<FoodLog>> = _foodLogs.asStateFlow()

    override suspend fun createFoodLog(
        userId: UUID,
        foodId: UUID,
        quantityGrams: BigDecimal,
        loggedAt: String?
    ): Result<FoodLog> {
        return try {
            val request = CreateFoodLogRequest(
                userId = userId,
                foodId = foodId,
                quantityGrams = quantityGrams,
                loggedAt = loggedAt
            )
            val response = api.createFoodLog(request)
            val foodLog = response.toDomain()

            _foodLogs.update { currentList -> currentList + foodLog }

            Result.success(foodLog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserFoodLogs(userId: UUID): Result<List<FoodLog>> {
        return try {
            val response = api.getUserFoodLogs(userId.toString())
            val foodLogs = response.map { it.toDomain() }

            _foodLogs.value = foodLogs

            Result.success(foodLogs)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFoodLogTimestamp(foodLogId: UUID, loggedAt: String): Result<FoodLog> {
        return try {
            val request = UpdateFoodLogTimestampRequest(loggedAt = loggedAt)
            val response = api.updateFoodLogTimestamp(foodLogId.toString(), request)
            val updatedFoodLog = response.toDomain()

            _foodLogs.update { currentList ->
                currentList.map { if (it.id == foodLogId) updatedFoodLog else it }
            }

            Result.success(updatedFoodLog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun FoodLogResponse.toDomain(): FoodLog {
    return FoodLog(
        id = id,
        userId = userId,
        foodId = foodId,
        name = name,
        quantityGrams = quantityGrams,
        loggedAt = loggedAt,
        calories = calories,
        proteinGrams = proteinGrams,
        carbsGrams = carbsGrams,
        fatsGrams = fatsGrams
    )
}
