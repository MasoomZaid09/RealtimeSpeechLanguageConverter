package com.masoom.speechlanguageconverter.domain.helper

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AudioRecordHelper {

    private val sampleRate = 16000
    private val bufferSize =
        AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

   private var recorder : AudioRecord? = null

    private var recordJob: Job? = null

    fun start(onChunk: (String) -> Unit) {

        recorder = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        recorder?.startRecording()
        val buffer = ByteArray(bufferSize)

        recordJob = CoroutineScope(Dispatchers.IO).launch {
            while (recorder?.recordingState ==
                AudioRecord.RECORDSTATE_RECORDING
            ) {
                Log.i("STTScreen", "Recording audio...")
                val read = recorder?.read(buffer, 0, buffer.size)
                if (read != null && read > 0) {
                    val base64 =
                        Base64.encodeToString(
                            buffer.copyOf(read),
                            Base64.NO_WRAP
                        )
                    onChunk(base64)
                }
            }
        }
        recordJob?.start()
    }

    fun stop() {
        Log.i("STT", "stop called")
        recorder?.stop()
        recorder?.release()
        if (recordJob?.isActive == true) recordJob?.cancel()
    }
}
