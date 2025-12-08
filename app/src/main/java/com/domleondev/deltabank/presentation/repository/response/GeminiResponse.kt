package com.domleondev.deltabank.presentation.repository.response

import com.domleondev.deltabank.presentation.repository.request.GeminiPart

data class GeminiResponse(
    val candidates: List<GeminiCandidate>
)

data class GeminiCandidate(
    val content: GeminiCandidateContent
)

data class GeminiCandidateContent(
    val parts: List<GeminiPart>
)
