package com.example.macropp.domain.repository

import com.example.macropp.data.remote.dto.CreateWeighInRequest
import com.example.macropp.domain.model.WeighIn
import java.util.UUID

interface WeighInRepository {
    suspend fun createWeighIn(createWeighInRequest: CreateWeighInRequest): Result<WeighIn>
    suspend fun getWeighIn(id: UUID): Result<WeighIn>
}
