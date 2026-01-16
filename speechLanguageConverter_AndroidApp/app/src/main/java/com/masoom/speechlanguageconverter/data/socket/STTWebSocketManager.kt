package com.masoom.speechlanguageconverter.data.socket

import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import okhttp3.*
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class STTWebSocketManager {

    private val client = OkHttpClient.Builder()
        .pingInterval(10, TimeUnit.SECONDS)
        .build()

    private var webSocket: WebSocket? = null

    // state for connection status
    private val _connectionStatus = MutableStateFlow(false)
    val connectionStatus: StateFlow<Boolean> = _connectionStatus

    private val _finalTranscript = MutableStateFlow("")
    val finalTranscript: StateFlow<String> = _finalTranscript

    private val _partialTranscript = MutableStateFlow("")
    val partialTranscript: StateFlow<String> = _partialTranscript

    fun resetSetup(){
        _finalTranscript.tryEmit("")
        _partialTranscript.tryEmit("")
    }

    fun connect() {
        try {
            val request = Request.Builder()
                .url("ws://192.168.1.2:8080")
//                .url("ws://localhost:8080")
                .build()

            webSocket = client.newWebSocket(request, socketListener)
        }catch (e: Exception){
            e.printStackTrace()
            _connectionStatus.tryEmit(false)
        }
    }

    fun sendAudioChunk(base64: String, isLast: Boolean = false) {
        val json = JSONObject().apply {
            put("audioChunk", base64)
            put("isLast", isLast)
        }

        webSocket?.send(json.toString())
    }

    fun close() {
        webSocket?.close(1000, "Stopped by user")
        webSocket = null
    }

    private val socketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            if (response.code == 101) _connectionStatus.tryEmit(true)
            else _connectionStatus.tryEmit(false)

            Log.i("STT", "WebSocket Connected ${response.code}")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            try {
                val json = JSONObject(text)
                val transcript = json.optString("transcript")
                val isFinal = json.optBoolean("isFinal")
                Log.i("STT", transcript)
                if (transcript.isNotEmpty()) {
                    if (isFinal) {
                        _finalTranscript.value = (_finalTranscript.value + " " + transcript).trim()
                        _partialTranscript.value = ""
                    } else {
                        _partialTranscript.value = transcript
                    }
                }
            } catch (e: Exception) {
                Log.e("STT", "Parse error", e)
            }
        }

        override fun onFailure(
            webSocket: WebSocket,
            t: Throwable,
            response: Response?
        ) {
            Log.e("STT", "Error", t)
            _connectionStatus.tryEmit(false)
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            _connectionStatus.tryEmit(false)
            Log.i("STT", "Closed: $reason")
        }
    }
}
