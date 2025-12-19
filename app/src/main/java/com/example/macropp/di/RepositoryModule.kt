package com.example.macropp.di

import com.example.macropp.data.remote.UserApi
import com.example.macropp.data.remote.UserGoalApi
import com.example.macropp.data.repository.UserGoalRepositoryImpl
import com.example.macropp.data.repository.UserRepositoryImpl
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
    fun provideUserGoalRepository(api: UserGoalApi): UserGoalRepository {
        return UserGoalRepositoryImpl(api)
    }
}