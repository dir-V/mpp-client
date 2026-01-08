package com.example.macropp.data.repository

import android.graphics.Bitmap
import com.example.macropp.data.remote.FoodLogApi
import com.example.macropp.data.remote.GeminiService // Ensure this is imported
import com.example.macropp.data.remote.dto.CreateFoodLogRequest
import com.example.macropp.data.remote.dto.CreateGeminiRequest
import com.example.macropp.data.remote.dto.FoodLogResponse
import com.example.macropp.data.remote.dto.UpdateFoodLogTimestampRequest
import com.example.macropp.domain.model.FoodLog
import com.example.macropp.domain.repository.FoodLogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONObject
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import kotlin.plus

class FoodLogRepositoryImpl @Inject constructor(
    private val api: FoodLogApi,
    private val geminiService: GeminiService
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

    override suspend fun getUserFoodLogs(userId: UUID, date: String?): Result<List<FoodLog>> {
        return try {
            val response = api.getUserFoodLogs(userId.toString(), date)
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

    override suspend fun deleteFoodLog(foodLogId: UUID): Result<Unit> {
        return try {
            val response = api.deleteFoodLog(foodLogId.toString())

            if (response.isSuccessful) {
                _foodLogs.update { currentList ->
                    currentList.filter { it.id != foodLogId }
                }
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to delete food log: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun analyzeFoodPhoto(image: Bitmap): Result<FoodLog> {
        return try {

            val jsonString = geminiService.analyzeFoodImage(image)
            val json = JSONObject(jsonString)

            if (json.length() == 0) {
                return Result.failure(Exception("Could not identify food in image"))
            }

            val estimatedLog = FoodLog(
                id = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                foodId = null,
                name = "AI Scanned Food",
                quantityGrams = null,
                loggedAt = null,
                calories = json.optInt("calories", 0),
                proteinGrams = BigDecimal(json.optInt("protein", 0)),
                carbsGrams = BigDecimal(json.optInt("carbs", 0)),
                fatsGrams = BigDecimal(json.optInt("fats", 0))
            )

            Result.success(estimatedLog)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun createQuickLog(
        userId: UUID,
        name: String,
        calories: Int,
        protein: BigDecimal,
        carbs: BigDecimal,
        fats: BigDecimal
    ): Result<FoodLog> {
        return try {
            val request = CreateGeminiRequest(
                userId = userId,
                name = name,
                calories = calories,
                proteinGrams = protein,
                carbsGrams = carbs,
                fatsGrams = fats,
                loggedAt = null
            )
            // Call the new API endpoint
            val response = api.createQuickLog(request)
            val foodLog = response.toDomain()

            // Update the local flow so the UI updates immediately
            _foodLogs.update { currentList -> currentList + foodLog }

            Result.success(foodLog)
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