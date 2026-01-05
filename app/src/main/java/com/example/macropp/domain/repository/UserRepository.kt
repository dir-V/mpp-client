package com.example.macropp.domain.repository

import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.domain.model.User
import java.util.UUID

interface UserRepository {
    suspend fun createUser(createUserRequest: CreateUserRequest): Result<User>
    suspend fun getUser(id: UUID): Result<User>
    suspend fun loginUser(email: String): Result<User>
}
