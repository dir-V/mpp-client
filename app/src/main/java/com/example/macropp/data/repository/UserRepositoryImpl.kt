package com.example.macropp.data.repository

import com.example.macropp.data.remote.UserApi
import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.data.remote.dto.UserResponse
import com.example.macropp.domain.model.User
import com.example.macropp.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class UserRepositoryImpl (
    private val api: UserApi
) : UserRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    override suspend fun createUser(createUserRequest: CreateUserRequest): Result<User> {
        return try{
            val requestDto = createUserRequest.toRequestDto()

            val responseDto = api.createUser(requestDto)

            val user = responseDto.toDomain()
            _currentUser.value = user

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(id: UUID): Result<User> {
        return try {
            val responseDto = api.getUser(id)
            val user = responseDto.toDomain()
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

// Mappers
private fun CreateUserRequest.toRequestDto(): CreateUserRequest {
    return CreateUserRequest(
        email = email,
        heightCm = heightCm
    )
}


private fun UserResponse.toDomain(): User {
    return User(
        id = id,
        email = email,
        heightCm = heightCm,
        joinedDate = joinedDate
    )
}