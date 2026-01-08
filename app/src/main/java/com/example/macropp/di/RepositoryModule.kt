package com.example.macropp.di

import com.example.macropp.data.remote.FoodApi
import com.example.macropp.data.remote.FoodLogApi
import com.example.macropp.data.remote.GeminiService
import com.example.macropp.data.remote.UserApi
import com.example.macropp.data.remote.UserGoalApi
import com.example.macropp.data.repository.FoodLogRepositoryImpl
import com.example.macropp.data.repository.UserGoalRepositoryImpl
import com.example.macropp.data.repository.FoodRepositoryImpl
import com.example.macropp.data.remote.WeighInApi
import com.example.macropp.data.repository.UserRepositoryImpl
import com.example.macropp.domain.repository.FoodLogRepository
import com.example.macropp.domain.repository.FoodRepository
import com.example.macropp.domain.repository.UserGoalRepository
import com.example.macropp.data.repository.WeighInRepositoryImpl
import com.example.macropp.domain.repository.UserRepository
import com.example.macropp.domain.repository.WeighInRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideUserRepository(api: UserApi): UserRepository {
        return UserRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideFoodRepository(api: FoodApi): FoodRepository {
        return FoodRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideUserGoalRepository(api: UserGoalApi): UserGoalRepository {
        return UserGoalRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideFoodLogRepository(
        api: FoodLogApi,
        geminiService: GeminiService // 1. Add the dependency here
    ): FoodLogRepository {
        return FoodLogRepositoryImpl(api, geminiService) // 2. Pass it to the constructor
    }

    @Provides
    @Singleton
    fun provideWeighInRepository(api: WeighInApi): WeighInRepository {
        return WeighInRepositoryImpl(api)
    }
}