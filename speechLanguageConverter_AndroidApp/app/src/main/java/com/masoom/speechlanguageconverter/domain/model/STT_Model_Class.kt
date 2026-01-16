package com.masoom.speechlanguageconverter.domain.model

data class STTRequest(
    val audioChunk: String,
    val isLast: Boolean
)

data class STTResponse(
    val status: String,
    val transcript: String?,
    val isFinal: Boolean
)
