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
import com.example.musicapp2026.ui.screens.*
import com.example.musicapp2026.ui.theme.MusicAppTheme
import com.example.musicapp2026.ui.viewmodel.MusicViewModel
import com.example.musicapp2026.ui.viewmodel.ThemeViewModel
import com.example.musicapp2026.ui.viewmodel.PlaylistUiModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PlaylistActivity : ComponentActivity() {

    private val viewModel: MusicViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val currentTheme by themeViewModel.currentTheme.collectAsState()
            
            MusicAppTheme(themeType = currentTheme) {
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.PlaylistGrid) }
                val playlists by viewModel.playlists.collectAsState()

                MusicAppDrawer(
                    drawerState = drawerState,
                    onSettingsClick = { 
                        currentScreen = Screen.Settings
                        scope.launch { drawerState.close() }
                    },
                    onInfoClick = { /* TODO */ }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        BackHandler(enabled = currentScreen != Screen.PlaylistGrid) {
                            currentScreen = when (currentScreen) {
                                is Screen.SongList -> Screen.PlaylistGrid
                                Screen.SongDetail -> Screen.PlaylistGrid
                                Screen.CreatePlaylist -> Screen.PlaylistGrid
                                Screen.Settings -> Screen.PlaylistGrid
                                else -> Screen.PlaylistGrid
                            }
                        }

                        when (val screen = currentScreen) {
                            Screen.PlaylistGrid -> {
                                PlaylistScreen(
                                    viewModel = viewModel,
                                    onPlaylistClick = { playlist ->
                                        currentScreen = Screen.SongList(playlist.id)
                                    },
                                    onCreatePlaylistClick = {
                                        currentScreen = Screen.CreatePlaylist
                                    },
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onOpenPlayer = {
                                        currentScreen = Screen.SongDetail
                                    }
                                )
                            }
                            is Screen.SongList -> {
                                val playlist = playlists.find { it.id == screen.playlistId }
                                if (playlist != null) {
                                    MainScreen(
                                        viewModel = viewModel,
                                        title = playlist.name,
                                        songs = playlist.songs,
                                        onBack = { currentScreen = Screen.PlaylistGrid },
                                        onMenuClick = { scope.launch { drawerState.open() } },
                                        onOpenPlayer = { currentScreen = Screen.SongDetail }
                                    )
                                }
                            }
                            Screen.SongDetail -> {
                                SongDetailScreen(
                                    viewModel = viewModel,
                                    onBack = {
                                        currentScreen = Screen.PlaylistGrid
                                    },
                                    onMenuClick = { scope.launch { drawerState.open() } }
                                )
                            }
                            Screen.CreatePlaylist -> {
                                CreatePlaylistScreen(
                                    viewModel = viewModel,
                                    onBack = { currentScreen = Screen.PlaylistGrid },
                                    onConfirm = { name, songIds ->
                                        val selectedSongs = songIds.map { id -> viewModel.songs.value.first { it.id == id } }
                                        viewModel.createPlaylist(name, selectedSongs)
                                        currentScreen = Screen.PlaylistGrid
                                    },
                                    onMenuClick = { scope.launch { drawerState.open() } },
                                    onOpenPlayer = { currentScreen = Screen.SongDetail }
                                )
                            }
                            Screen.Settings -> {
                                SettingsScreen(
                                    themeViewModel = themeViewModel,
                                    onBack = { currentScreen = Screen.PlaylistGrid },
                                    onMenuClick = { scope.launch { drawerState.open() } }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

sealed class Screen {
    data object PlaylistGrid : Screen()
    data class SongList(val playlistId: Long) : Screen()
    data object SongDetail : Screen()
    data object CreatePlaylist : Screen()
    data object Settings : Screen()
}
