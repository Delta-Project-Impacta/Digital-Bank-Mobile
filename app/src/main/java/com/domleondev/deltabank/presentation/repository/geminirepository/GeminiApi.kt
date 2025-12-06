package com.domleondev.deltabank.presentation.repository.geminirepository

import com.domleondev.deltabank.presentation.repository.request.GeminiRequest
import com.domleondev.deltabank.presentation.repository.response.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApi {

    @POST("models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(@Body request: GeminiRequest): GeminiResponse

}