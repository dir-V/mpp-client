package com.example.macropp.data.remote

import com.example.macropp.data.remote.dto.CreateFoodLogRequest
import com.example.macropp.data.remote.dto.CreateGeminiRequest
import com.example.macropp.data.remote.dto.FoodLogResponse
import com.example.macropp.data.remote.dto.UpdateFoodLogTimestampRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodLogApi {
    @POST("api/food-logs")
    suspend fun createFoodLog(
        @Body request: CreateFoodLogRequest
    ): FoodLogResponse

    @GET("api/food-logs/user/{userId}")
    suspend fun getUserFoodLogs(
        @Path("userId") userId: String,
        @Query("date") date: String? = null
    ): List<FoodLogResponse>

    @PUT("api/food-logs/{id}/timestamp")
    suspend fun updateFoodLogTimestamp(
        @Path("id") foodLogId: String,
        @Body request: UpdateFoodLogTimestampRequest
    ): FoodLogResponse

    @DELETE("api/food-logs/{id}")
    suspend fun deleteFoodLog(@Path("id") foodLogId: String): Response<Unit>
    suspend fun getUserFoodLogs(@Path("userId") userId: String): List<FoodLogResponse>

    @POST("api/food-logs/quick-add") // Ensure this matches your backend controller path
    suspend fun createQuickLog(@Body request: CreateGeminiRequest): FoodLogResponse
}
