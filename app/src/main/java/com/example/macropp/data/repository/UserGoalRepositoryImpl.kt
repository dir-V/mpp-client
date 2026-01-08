package com.example.macropp.data.repository

import com.example.macropp.data.remote.UserGoalApi
import com.example.macropp.domain.repository.UserGoalRepository
import com.example.macropp.data.remote.dto.CreateUserGoalRequest
import com.example.macropp.data.remote.dto.UserGoalResponse
import com.example.macropp.domain.model.UserGoal
import java.util.UUID
import javax.inject.Inject

class UserGoalRepositoryImpl @Inject constructor (
    private val api: UserGoalApi
    ): UserGoalRepository{
        override suspend fun createUserGoal(createUserGoalRequest: CreateUserGoalRequest): Result<UserGoal> {
            return try {
                val requestDto = createUserGoalRequest.toRequestDto()

                val responseDto = api.createUserGoal(requestDto)

                val userGoal = responseDto.toDomain()

                Result.success(userGoal)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

        override suspend fun getActiveGoal(id: UUID): Result<UserGoal> {
            return try {
                val responseDto = api.getActiveGoal(id)
                val userGoal = responseDto.toDomain()  // Transform here!
                Result.success(userGoal)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
}

private fun CreateUserGoalRequest.toRequestDto(): CreateUserGoalRequest{
    return CreateUserGoalRequest(
        userId = userId,
        goalType = goalType,
        targetCalories = targetCalories,
        targetProteinGrams = targetProteinGrams,
        targetCarbsGrams = targetCarbsGrams,
        targetFatsGrams = targetFatsGrams
    )
}

private fun UserGoalResponse.toDomain(): UserGoal {
    return UserGoal(
        id = id,
        userId = userId,
        goalType = goalType,
        targetCalories = targetCalories,
        targetProteinGrams = targetProteinGrams,
        targetCarbsGrams = targetCarbsGrams,
        targetFatsGrams = targetFatsGrams,
        startDate = startDate,
        endDate = endDate,
        isActive = isActive,
        createdAt = createdAt
    )
}
