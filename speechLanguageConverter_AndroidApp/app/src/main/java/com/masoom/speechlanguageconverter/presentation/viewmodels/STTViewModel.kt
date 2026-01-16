package com.masoom.speechlanguageconverter.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.masoom.speechlanguageconverter.data.socket.STTWebSocketManager
import com.masoom.speechlanguageconverter.domain.model.TranslateResponse
import com.masoom.speechlanguageconverter.domain.repo.RepoInterface
import com.masoom.speechlanguageconverter.domain.state_manangment.ApiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class STTViewModel(private val repo: RepoInterface) : ViewModel() {

    // api implementation related section
    private val _translatedText = MutableStateFlow< ApiState<String?>>(ApiState.Idle)
    val translatedText: StateFlow<ApiState<String?>> = _translatedText

    private val _translatedSpeech = MutableStateFlow< ApiState<ByteArray?>>(ApiState.Idle)
    val translatedSpeech: StateFlow< ApiState<ByteArray?>> = _translatedSpeech



    fun translate(text: String) = viewModelScope.launch {

        try {
            _translatedText.tryEmit(ApiState.Loading)
            val response = repo.translate(text)
            _translatedText.tryEmit(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _translatedText.tryEmit(ApiState.Error(e.message ?: "Unknown Error"))
        }
    }

    fun textToSpeech(text: String) = viewModelScope.launch {
        try {
            _translatedSpeech.tryEmit(ApiState.Loading)
            val response = repo.textToSpeech(text)
            _translatedSpeech.tryEmit(response)
        } catch (e: Exception) {
            e.printStackTrace()
            _translatedSpeech.tryEmit(ApiState.Error(e.message ?: "Unknown Error"))
        }
    }


    // Web socket implementation related section
    private val wsManager = STTWebSocketManager()
    val finalTranscript: StateFlow<String> = wsManager.finalTranscript
    val partialTranscript: StateFlow<String> = wsManager.partialTranscript
    val connectionStatus: StateFlow<Boolean> = wsManager.connectionStatus

    fun resetSetup() {
        wsManager.resetSetup()
    }


    fun startSTT() {
        wsManager.connect()
    }

    fun sendAudio(base64: String) {
        wsManager.sendAudioChunk(base64)
    }

    fun stopSTT() {
        wsManager.sendAudioChunk("", isLast = true)
        wsManager.close()
    }

    override fun onCleared() {
        super.onCleared()
        wsManager.close()
    }
}