package com.example.musicapp2026.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.musicapp2026.ui.screens.CreatePlaylistScreen
import com.example.musicapp2026.ui.screens.MainScreen
import com.example.musicapp2026.ui.screens.PlaylistScreen
import com.example.musicapp2026.ui.screens.SongDetailScreen
import com.example.musicapp2026.ui.viewmodel.MusicViewModel
import com.example.musicapp2026.ui.viewmodel.PlaylistUiModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlaylistActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.PlaylistGrid) }

                    BackHandler(enabled = currentScreen != Screen.PlaylistGrid) {
                        currentScreen = when (currentScreen) {
                            is Screen.SongList -> Screen.PlaylistGrid
                            Screen.SongDetail -> Screen.PlaylistGrid
                            Screen.CreatePlaylist -> Screen.PlaylistGrid
                            else -> Screen.PlaylistGrid
                        }
                    }

                    when (val screen = currentScreen) {
                        Screen.PlaylistGrid -> {
                            PlaylistScreen(
                                viewModel = viewModel,
                                onPlaylistClick = { playlist ->
                                    currentScreen = Screen.SongList(playlist)
                                },
                                onCreatePlaylistClick = {
                                    currentScreen = Screen.CreatePlaylist
                                },
                                onOpenPlayer = {
                                    currentScreen = Screen.SongDetail
                                }
                            )
                        }
                        is Screen.SongList -> {
                            MainScreen(
                                viewModel = viewModel,
                                title = screen.playlist.name,
                                songs = screen.playlist.songs,
                                onBack = { currentScreen = Screen.PlaylistGrid },
                                onOpenPlayer = { currentScreen = Screen.SongDetail }
                            )
                        }
                        Screen.SongDetail -> {
                            SongDetailScreen(
                                viewModel = viewModel,
                                onBack = {
                                    currentScreen = Screen.PlaylistGrid
                                }
                            )
                        }
                        Screen.CreatePlaylist -> {
                            CreatePlaylistScreen(
                                viewModel = viewModel,
                                onBack = { currentScreen = Screen.PlaylistGrid },
                                onConfirm = { name, songs ->
                                    viewModel.createPlaylist(name, songs)
                                    currentScreen = Screen.PlaylistGrid
                                },
                                onOpenPlayer = { currentScreen = Screen.SongDetail }
                            )
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    data object PlaylistGrid : Screen()
    data class SongList(val playlist: PlaylistUiModel) : Screen()
    data object SongDetail : Screen()
    data object CreatePlaylist : Screen()
}
