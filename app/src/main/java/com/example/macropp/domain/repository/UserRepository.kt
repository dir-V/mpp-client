package com.example.macropp.domain.repository

import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.domain.model.User
import kotlinx.coroutines.flow.StateFlow
import java.util.UUID

interface UserRepository {
    val currentUser: StateFlow<User?>

    suspend fun createUser(createUserRequest: CreateUserRequest): Result<User>
    suspend fun getUser(id: UUID): Result<User>
    suspend fun loginUser(email: String): Result<User>
}
