//package com.masoom.speechlanguageconverter.presentation.features
//
//import android.content.Context
//import android.util.Log
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextField
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.sp
//import com.masoom.speechlanguageconverter.domain.helper.AudioRecordHelper
//import com.masoom.speechlanguageconverter.domain.helper.playMp3
//import com.masoom.speechlanguageconverter.presentation.permission_handlers.rememberAudioPermission
//import com.masoom.speechlanguageconverter.presentation.viewmodels.STTViewModel
//
//@Composable
//fun SpeechToTextScreenBackup(viewModel: STTViewModel, context: Context) {
//
//    val micPermission = rememberAudioPermission()
//    val finalTranscript by viewModel.finalTranscript.collectAsState()
//    val partialTranscript by viewModel.partialTranscript.collectAsState()
//    val connectionStatus by viewModel.connectionStatus.collectAsState()
//
//    val translatedText by viewModel.translatedText.collectAsState()
//    val translatedSpeech by viewModel.translatedSpeech.collectAsState()
//
//    val streamer = remember { AudioRecordHelper() }
//
//    LaunchedEffect(translatedSpeech) {
//        translatedSpeech?.let { audioBytes ->
//            playMp3(audioBytes, context)
//        }
//    }
//
//    LaunchedEffect(connectionStatus) {
//        if (connectionStatus) {
//            streamer.start { chunk ->
//                viewModel.sendAudio(chunk)
//            }
//        } else {
//            streamer.stop()
//        }
//    }
//
//    Column(modifier = Modifier
//        .fillMaxSize()
//        .background(Color.Black)) {
//
//        // Step 1: Display Transcribed Text
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.45f)
//                .padding(bottom = 10.dp)
//                .background(
//                    color = Color.Black,
//                    shape = RoundedCornerShape(10.dp)
//                )
//        ) {
//            TextField(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(10.dp),
//                value = if (finalTranscript == "" && partialTranscript == "") "Transcription will be here" else "$finalTranscript $partialTranscript",
//
//                onValueChange = {
//                },
//                textStyle = androidx.compose.ui.text.TextStyle(
//                    color = Color.Black,
//                    fontSize = 18.dp.value.sp
//                ),
//                readOnly = true
//            )
//        }
//
//        //  Step 2: section for showing translated text can be added here
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.45f)
//                .padding(bottom = 10.dp)
//                .background(
//                    color = Color.Black,
//                    shape = RoundedCornerShape(10.dp)
//                )
//        ) {
//            TextField(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(10.dp),
//                value = translatedText?.let {
//                    it.reply.candidates?.let { candidates ->
//                        candidates[0].content?.parts?.get(0)?.text
//                    } ?: run { "Something went wrong.." }
//                } ?: "Translation will appear here",
//
//                onValueChange = {
//                },
//                textStyle = androidx.compose.ui.text.TextStyle(
//                    color = Color.Black,
//                    fontSize = 18.dp.value.sp
//                ),
//                readOnly = true
//            )
//        }
//
//        // buttons section
//        Row(modifier = Modifier
//            .fillMaxWidth()
//            .weight(0.10f)
//            .padding(horizontal = 10.dp)) {
//
//            // speak button UI
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .weight(0.32f)
//                    .align(Alignment.CenterVertically)
//                    .background(
//                        color = if (connectionStatus) Color.Yellow else Color.Red,
//                        shape = RoundedCornerShape(10.dp)
//                    )
//                    .clickable {
//                        if (micPermission) {
//                            Log.i("STTScreen", "$connectionStatus & micPermission: $micPermission")
//                            if (!connectionStatus) {
//                                viewModel.startSTT()
//                            } else {
//                                viewModel.stopSTT()
//                            }
//                        }
//                    }) {
//
//                Text(
//                    if (connectionStatus) "Listening..." else "SPEAK",
//                    fontSize = 20.sp, color = if (connectionStatus) Color.Black else Color.White,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//
//            Spacer(modifier = Modifier
//                .weight(0.02f)
//                .fillMaxHeight())
//
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .weight(0.432f)
//                    .align(Alignment.CenterVertically)
//                    .background(
//                        color = Color.Yellow,
//                        shape = RoundedCornerShape(10.dp)
//                    )
//                    .clickable {
//                        viewModel.sendToTranslationAPI(finalTranscript)
//                    }) {
//
//                Text(
//                    "TRANSLATE", fontSize = 20.sp, color = Color.Black,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//
//            Spacer(modifier = Modifier
//                .weight(0.02f)
//                .fillMaxHeight())
//
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .weight(0.432f)
//                    .align(Alignment.CenterVertically)
//                    .background(
//                        color = Color.Yellow,
//                        shape = RoundedCornerShape(10.dp)
//                    )
//                    .clickable {
//                        val text = translatedText?.let {
//                            it.reply.candidates?.let { candidates ->
//                                candidates[0].content?.parts?.get(0)?.text
//                            } ?: run { "" }
//                        } ?: ""
//                        viewModel.sendTranslationToTTS(text)
//                    }) {
//
//                Text(
//                    "PLAY", fontSize = 20.sp, color = Color.Black,
//                    fontWeight = FontWeight.Bold,
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//        }
//
//        Spacer(
//            modifier = Modifier
//                .fillMaxWidth()
//                .weight(0.05f)
//        )
//
//    }
//
//
//}