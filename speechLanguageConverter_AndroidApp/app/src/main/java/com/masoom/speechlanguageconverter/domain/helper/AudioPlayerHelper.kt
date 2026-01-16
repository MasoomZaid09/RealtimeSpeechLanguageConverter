package com.masoom.speechlanguageconverter.domain.helper

import android.content.Context
import android.media.MediaPlayer
import java.io.File

fun playMp3(bytes: ByteArray, context: Context) {
    val file = File(context.cacheDir, "tts_audio.mp3")
    file.writeBytes(bytes)

    val player = MediaPlayer().apply {
        setDataSource(file.absolutePath)
        prepare()
        start()
    }

    player.setOnCompletionListener {
        it.release()
    }
}