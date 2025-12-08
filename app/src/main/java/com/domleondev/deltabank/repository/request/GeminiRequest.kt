package com.domleondev.deltabank.repository.request

data class GeminiRequest(
    val model: String = "gemini-2.5-flash",
    val contents: List<GeminiContent>
)

data class GeminiContent(
    val role: String,
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)
