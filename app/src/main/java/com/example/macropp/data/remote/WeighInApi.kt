package com.example.macropp.data.remote

import com.example.macropp.data.remote.dto.CreateWeighInRequest
import com.example.macropp.data.remote.dto.WeighInResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.UUID

interface WeighInApi {

    @POST("api/weigh-ins")
    suspend fun createWeighIn(
        @Body request: CreateWeighInRequest
    ): WeighInResponse

    @GET("api/weigh-ins/{id}")
    suspend fun getWeighIn(
        @Path("id") id: UUID
    ): WeighInResponse

    //  GET http://localhost:8080/api/weigh-ins/user/{userId}
    // get all weigh-ins created by a specific user
    @GET("api/weigh-ins/user/{userId}")
    suspend fun getUserWeighIns(
        @Path("userId") userId: UUID
    ): List<WeighInResponse>

}
