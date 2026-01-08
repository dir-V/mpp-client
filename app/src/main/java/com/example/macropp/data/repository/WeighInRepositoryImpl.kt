package com.example.macropp.data.repository

import androidx.compose.ui.Modifier
import com.example.macropp.data.remote.WeighInApi
import com.example.macropp.data.remote.dto.CreateUserGoalRequest
import com.example.macropp.data.remote.dto.CreateWeighInRequest
import com.example.macropp.data.remote.dto.WeighInResponse
import com.example.macropp.domain.model.WeighIn
import com.example.macropp.domain.repository.WeighInRepository
import java.util.UUID

class WeighInRepositoryImpl(
    private val api: WeighInApi
) : WeighInRepository {

    override suspend fun createWeighIn(createWeighInRequest: CreateWeighInRequest): Result<WeighIn> {
        return try {
            val requestDto = createWeighInRequest.toRequestDto()
            val responseDto = api.createWeighIn(createWeighInRequest)
            val weighIn = responseDto.toDomain()
            Result.success(weighIn)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getWeighIn(id: UUID): Result<WeighIn> {
        return try {
            val responseDto = api.getWeighIn(id)
            val weighIn = responseDto.toDomain()
            Result.success(weighIn)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

private fun CreateWeighInRequest.toRequestDto(): CreateWeighInRequest{
    return CreateWeighInRequest(
        userId = userId,
        weightKg = weightKg,
        weightDate = weightDate
    )
}
private fun WeighInResponse.toDomain(): WeighIn {
    return WeighIn(
        id = id, // whateva
        userId = userId,
        weightKg = weightKg,
        weightDate = weightDate
    )
}
