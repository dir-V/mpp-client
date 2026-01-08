package com.example.macropp.di

import com.example.macropp.data.remote.FoodApi
import com.example.macropp.data.remote.FoodLogApi
import com.example.macropp.data.remote.UserApi
import com.example.macropp.data.remote.UserGoalApi
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "http://10.113.132.147:8080/"

    val gson = GsonBuilder()
        .registerTypeAdapter(LocalDate::class.java, JsonDeserializer { json, _, _ ->
            LocalDate.parse(json.asJsonPrimitive.asString)
        })
        .registerTypeAdapter(LocalDateTime::class.java, JsonDeserializer { json, _, _ ->
            LocalDateTime.parse(json.asJsonPrimitive.asString)
        })
        .create()
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    @Provides
    @Singleton
    fun provideUserApi(retrofit: Retrofit): UserApi {
        return retrofit.create(UserApi::class.java)
    }

    @Provides
    @Singleton
    fun provideUserGoalApi(retrofit: Retrofit): UserGoalApi {
        return retrofit.create(UserGoalApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFoodApi(retrofit: Retrofit): FoodApi {
        return retrofit.create(FoodApi::class.java)
    }

    @Provides
    @Singleton
    fun provideFoodLogApi(retrofit: Retrofit): FoodLogApi {
        return retrofit.create(FoodLogApi::class.java)
    }
}
