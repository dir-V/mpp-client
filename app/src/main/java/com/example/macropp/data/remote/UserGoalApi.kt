package com.example.macropp.data.remote

import com.example.macropp.data.remote.dto.CreateUserGoalRequest
import com.example.macropp.data.remote.dto.UserGoalResponse
import com.example.macropp.data.remote.dto.UserResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface UserGoalApi {

    @POST("api/user-goals")
    suspend fun createUserGoal(
        @Body request: CreateUserGoalRequest
    ): UserGoalResponse


    @GET("api/user-goals/{userId}/active")
    suspend fun getActiveGoal(
        @Path("userId") id: UUID
    ): UserGoalResponse

    // CHECK THIS
    @GET("api/user-goals/{userId}/has-active")
    suspend fun checkHasActiveGoal(
        @Path("userId") id: UUID
    ): Boolean

}

