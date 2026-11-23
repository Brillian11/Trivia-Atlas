package com.example.data.remote

import com.example.data.model.TriviaApiQuestionDto
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface TriviaApiService {
    @GET("v2/questions")
    suspend fun getQuestions(
        @Query("limit") limit: Int = 20,
        @Query("categories") categories: String? = null,
        @Query("difficulties") difficulties: String? = null,
        @Query("types") types: String? = null,
        @Header("Cache-Control") cacheControl: String = "no-cache"
    ): List<TriviaApiQuestionDto>

    companion object {
        const val BASE_URL = "https://the-trivia-api.com/"

        fun create(): TriviaApiService {
            val logger = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BASIC
            }
            val client = OkHttpClient.Builder()
                .addInterceptor(logger)
                .connectTimeout(12, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(TriviaApiService::class.java)
        }
    }
}
