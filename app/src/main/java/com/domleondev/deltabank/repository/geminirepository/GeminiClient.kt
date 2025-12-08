package com.domleondev.deltabank.repository.geminirepository

import com.domleondev.deltabank.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object GeminiClient {

    private const val GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY

    private val okHttpClient = OkHttpClient.Builder()

        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val originalRequest = chain.request()

            val newRequest = originalRequest.newBuilder()
                .addHeader("Content-Type", "application/json")
                .addHeader("x-goog-api-key", GEMINI_API_KEY)
                .build()

            chain.proceed(newRequest)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/v1beta/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val geminiApi: GeminiApi by lazy {
        retrofit.create(GeminiApi::class.java)
    }
}