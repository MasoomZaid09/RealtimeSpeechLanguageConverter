package com.masoom.speechlanguageconverter.data.api

import com.masoom.speechlanguageconverter.domain.model.TTSRequest
import com.masoom.speechlanguageconverter.domain.model.TranslateRequest
import com.masoom.speechlanguageconverter.domain.model.TranslateResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ApiInterface {

    @POST("chat")
    suspend fun translateApi(
        @Body request: TranslateRequest
    ): TranslateResponse

    @POST("tts")
    @Headers("Content-Type: application/json")
    suspend fun textToSpeech(
        @Body request: TTSRequest
    ): Response<ResponseBody>
}