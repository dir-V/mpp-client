package com.example.macropp.data.remote

import com.example.macropp.data.remote.dto.CreateFoodRequest
import com.example.macropp.data.remote.dto.FoodResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface FoodApi {
    @POST("api/foods")
    suspend fun createFood(
        @Body request: CreateFoodRequest
    ): FoodResponse

    @GET("api/foods/{foodId}")
    suspend fun getFood(@Path("foodId") foodId: String): FoodResponse

    @GET("api/foods/user/{userId}")
    suspend fun getUserFoods(@Path("userId") userId: String): List<FoodResponse>
}