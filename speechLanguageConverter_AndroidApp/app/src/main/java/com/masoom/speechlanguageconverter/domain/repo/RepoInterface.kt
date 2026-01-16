package com.masoom.speechlanguageconverter.domain.repo

import com.masoom.speechlanguageconverter.domain.model.TranslateResponse
import com.masoom.speechlanguageconverter.domain.state_manangment.ApiState

interface RepoInterface {

    suspend fun translate(text: String): ApiState<String>

    suspend fun textToSpeech(text: String): ApiState<ByteArray?>
}