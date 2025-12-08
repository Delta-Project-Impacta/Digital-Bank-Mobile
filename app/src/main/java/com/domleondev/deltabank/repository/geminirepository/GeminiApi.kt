package com.domleondev.deltabank.repository.geminirepository

import com.domleondev.deltabank.repository.request.GeminiRequest
import com.domleondev.deltabank.repository.response.GeminiResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface GeminiApi {

    @POST("models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(@Body request: GeminiRequest): GeminiResponse

}