package com.masoom.speechlanguageconverter

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.masoom.speechlanguageconverter.data.repo.RepoImplementation
import com.masoom.speechlanguageconverter.presentation.features.SpeechToTextScreen
import com.masoom.speechlanguageconverter.presentation.viewmodels.STTViewModel

class MainActivity : ComponentActivity() {

    private val viewModel = STTViewModel(RepoImplementation())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { MainScreen(this@MainActivity,viewModel) }
    }
}

@Composable
fun MainScreen(ctx: Context, viewModel: STTViewModel) {

    Box(modifier = Modifier.fillMaxSize().background(Color.White).padding(WindowInsets.systemBars.asPaddingValues()))  {
        SpeechToTextScreen(viewModel, ctx)
    }
}


