package com.example.macropp.domain.repository

import com.example.macropp.data.remote.dto.CreateUserGoalRequest
import com.example.macropp.domain.model.UserGoal
import java.util.UUID

interface UserGoalRepository {

    suspend fun createUserGoal(createUserGoalRequest: CreateUserGoalRequest): Result<UserGoal>

    suspend fun getActiveGoal(id: UUID): Result<UserGoal>

}
