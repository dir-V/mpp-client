package com.example.macropp.data.remote

import com.example.macropp.data.remote.dto.CreateUserRequest
import com.example.macropp.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface UserApi {

    @POST("api/users")
    suspend fun createUser(
        @Body request: CreateUserRequest
    ): UserResponse

    @GET("api/users/{id}")
    suspend fun getUser(
        @Path("id") id: UUID
    ): UserResponse
}
