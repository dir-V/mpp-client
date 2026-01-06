package com.example.macropp.di

import com.example.macropp.data.remote.FoodApi
import com.example.macropp.data.remote.FoodLogApi
import com.example.macropp.data.remote.UserApi
import com.example.macropp.data.remote.UserGoalApi
import com.example.macropp.data.repository.FoodLogRepositoryImpl
import com.example.macropp.data.repository.UserGoalRepositoryImpl
import com.example.macropp.data.repository.FoodRepositoryImpl
import com.example.macropp.data.repository.UserRepositoryImpl
import com.example.macropp.domain.repository.FoodLogRepository
import com.example.macropp.domain.repository.FoodRepository
import com.example.macropp.domain.repository.UserGoalRepository
import com.example.macropp.domain.repository.UserRepository
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
    fun provideFoodLogRepository(api: FoodLogApi): FoodLogRepository {
        return FoodLogRepositoryImpl(api)
    }
}