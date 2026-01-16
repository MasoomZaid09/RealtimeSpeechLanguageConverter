package com.masoom.speechlanguageconverter.domain.model


data class TranslateRequest(
    val message: String
)

data class TranslateResponse(
    val reply: GeminiResponse? = null
)

data class GeminiResponse(
    val candidates: List<Candidate>? = null,
    val usageMetadata: UsageMetadata? = null,
    val modelVersion: String? = null,
    val responseId: String? = null
)

data class Candidate(
    val content: Content? = null,
    val finishReason: String? = null,
    val avgLogprobs: Double? = null
)

data class Content(
    val parts: List<Part>? = null,
    val role: String? = null
)

data class Part(
    val text: String? = null
)

data class UsageMetadata(
    val promptTokenCount: Int? = null,
    val candidatesTokenCount: Int? = null,
    val totalTokenCount: Int? = null
)
