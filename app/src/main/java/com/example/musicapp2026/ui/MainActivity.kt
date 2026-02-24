package com.example.musicapp2026.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.musicapp2026.controller.MusicController

class MainActivity : ComponentActivity() {

    private lateinit var musicController: MusicController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        musicController = MusicController(this)

        musicController.connect {
            // Conectado al MediaSessionService
        }

        setContent {
            MusicScreen(
                onPlay = { musicController.play() },
                onPause = { musicController.pause() }
            )
        }
    }

    override fun onDestroy() {
        musicController.release()
        super.onDestroy()
    }

    @Composable
    fun MusicScreen(
        onPlay: () -> Unit,
        onPause: () -> Unit
    ) {
        Surface {
            Column {
                Button(onClick = onPlay) {
                    Text("Play")
                }

                Button(onClick = onPause) {
                    Text("Pause")
                }
            }
        }
    }
}



