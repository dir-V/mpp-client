package com.example.macropp.di

import com.example.macropp.data.remote.UserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory // Or your preferred converter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // This is the default base URL for an app running in the Android Emulator
    // to connect to a server on the same machine (your computer's localhost).
    private const val BASE_URL = "http://10.0.2.2:8080/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            // Ensure you have the 'com.squareup.retrofit2:converter-gson' dependency
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }
}
