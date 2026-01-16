package com.masoom.speechlanguageconverter.data.repo

import android.util.Log
import com.masoom.speechlanguageconverter.data.api.RetrofitClient.api
import com.masoom.speechlanguageconverter.domain.helper.extractTranslatedText
import com.masoom.speechlanguageconverter.data.model.TTSRequest
import com.masoom.speechlanguageconverter.data.model.TranslateRequest
import com.masoom.speechlanguageconverter.domain.repo.RepoInterface
import com.masoom.speechlanguageconverter.domain.state_manangment.ApiState
import retrofit2.HttpException

class RepoImplementation : RepoInterface {

    override suspend fun translate(text: String): ApiState<String> {

        return try {
            ApiState.Loading

            val request = TranslateRequest(message = text)

            val response = api.translateApi(request)

            val translatedText = extractTranslatedText(response)
                ?: return ApiState.Error("Translation text not found")

            Log.i("RepoImplementation", "Translated Text: $translatedText")
            ApiState.Success(translatedText)

        } catch (e: HttpException) {
            ApiState.Error(
                message = e.message(),
                throwable = e,
                code = e.code()
            )
        } catch (e: Exception) {
            ApiState.Error(
                message = e.localizedMessage ?: "Something went wrong",
                throwable = e
            )
        }
    }

    override suspend fun textToSpeech(text: String): ApiState<ByteArray?> {

        return try {
            val response = api.textToSpeech(TTSRequest(text))

            if (!response.isSuccessful) {
                return ApiState.Error(
                    message = response.errorBody()?.string() ?: response.message(),
                    code = response.code()
                )
            }

            val audioBytes = response.body()?.use { it.bytes() }
                ?: return ApiState.Error("Empty audio response")

            ApiState.Success(audioBytes)

        } catch (e: Exception) {
            ApiState.Error(
                message = e.localizedMessage ?: "TTS failed",
                throwable = e
            )
        }
    }
}