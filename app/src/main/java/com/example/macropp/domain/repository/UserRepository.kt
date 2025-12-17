package com.example.macropp.domain.repository

import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.domain.model.User

interface UserRepository {
    suspend fun createUser(createUserRequest: CreateUserRequest): Result<User>
    suspend fun getUser(id: String): Result<User>
}
