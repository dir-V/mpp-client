package com.example.macropp.domain.repository

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

    suspend fun getUserFoodLogs(userId: UUID): Result<List<FoodLog>>
}
