package com.masoom.speechlanguageconverter.domain.model

data class TTSRequest(
    val text: String,
    val languageCode: String = "en-IN",
    val voiceName: String = "en-IN-Wavenet-A",
    val audioEncoding: String = "MP3"
)
