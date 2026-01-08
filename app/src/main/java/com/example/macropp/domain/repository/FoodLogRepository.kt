package com.example.macropp.domain.repository

import android.graphics.Bitmap
import com.example.macropp.domain.model.FoodLog
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.util.UUID

interface FoodLogRepository {
    val foodLogs: Flow<List<FoodLog>>

    suspend fun createFoodLog(
        userId: UUID,
        foodId: UUID,
        quantityGrams: BigDecimal,
        loggedAt: String?
    ): Result<FoodLog>

    suspend fun createQuickLog(
        userId: UUID,
        name: String,
        calories: Int,
        protein: BigDecimal,
        carbs: BigDecimal,
        fats: BigDecimal
    ): Result<FoodLog>

    suspend fun getUserFoodLogs(userId: UUID, date: String? = null): Result<List<FoodLog>>

    suspend fun updateFoodLogTimestamp(foodLogId: UUID, loggedAt: String): Result<FoodLog>

    suspend fun deleteFoodLog(foodLogId: UUID): Result<Unit>

    suspend fun analyzeFoodPhoto(image: Bitmap): Result<FoodLog>
}
