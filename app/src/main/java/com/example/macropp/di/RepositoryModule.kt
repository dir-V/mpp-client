package com.example.macropp.di

import com.example.macropp.data.remote.UserApi
import com.example.macropp.data.remote.WeighInApi
import com.example.macropp.data.repository.UserRepositoryImpl
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
    fun provideWeighInRepository(api: WeighInApi): WeighInRepository {
        return WeighInRepositoryImpl(api)
    }
}