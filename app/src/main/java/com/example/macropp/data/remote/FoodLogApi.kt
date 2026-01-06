package com.example.macropp.data.remote

import com.example.macropp.data.remote.dto.CreateFoodLogRequest
import com.example.macropp.data.remote.dto.FoodLogResponse
import com.example.macropp.data.remote.dto.UpdateFoodLogTimestampRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface FoodLogApi {
    @POST("api/food-logs")
    suspend fun createFoodLog(
        @Body request: CreateFoodLogRequest
    ): FoodLogResponse

    @GET("api/food-logs/user/{userId}")
    suspend fun getUserFoodLogs(@Path("userId") userId: String): List<FoodLogResponse>

    @PUT("api/food-logs/{id}/timestamp")
    suspend fun updateFoodLogTimestamp(
        @Path("id") foodLogId: String,
        @Body request: UpdateFoodLogTimestampRequest
    ): FoodLogResponse
}
