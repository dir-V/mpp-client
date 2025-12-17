package com.example.macropp.data.repository

import com.example.macropp.data.remote.UserApi
import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.data.remote.dto.UserResponse
import com.example.macropp.domain.model.User
import com.example.macropp.domain.repository.UserRepository

class UserRepositoryImpl (
    private val api: UserApi
) : UserRepository {
    override suspend fun createUser(createUserRequest: CreateUserRequest): Result<User> {
        return try{
            val requestDto = createUserRequest.toRequestDto()

            val responseDto = api.createUser(requestDto)

            val user = responseDto.toDomain()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUser(userId: Long): Result<User> {
        return try {
            val responseDto = api.getUser(userId)
            val user = responseDto.toDomain()  // Transform here!
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