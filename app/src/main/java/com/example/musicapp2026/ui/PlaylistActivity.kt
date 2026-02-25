package com.example.musicapp2026.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.example.musicapp2026.ui.screens.MainScreen
import com.example.musicapp2026.ui.screens.PlaylistScreen
import com.example.musicapp2026.ui.screens.SongDetailScreen
import com.example.musicapp2026.ui.viewmodel.MusicViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.PlaylistGrid) }

            MaterialTheme {
                BackHandler(enabled = currentScreen != Screen.PlaylistGrid) {
                    currentScreen = when (currentScreen) {
                        Screen.SongList -> Screen.PlaylistGrid
                        Screen.SongDetail -> Screen.SongList
                        else -> Screen.PlaylistGrid
                    }
                }

                when (currentScreen) {
                    Screen.PlaylistGrid -> {
                        PlaylistScreen(viewModel) { playlist ->
                            if (playlist.id == 1L) { // "Todas las canciones"
                                currentScreen = Screen.SongList
                            }
                        }
                    }
                    Screen.SongList -> {
                        MainScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.PlaylistGrid },
                            onOpenPlayer = { currentScreen = Screen.SongDetail }
                        )
                    }
                    Screen.SongDetail -> {
                        SongDetailScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.SongList }
                        )
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object PlaylistGrid : Screen()
    object SongList : Screen()
    object SongDetail : Screen()
}
