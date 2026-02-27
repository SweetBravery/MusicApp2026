package com.example.musicapp2026.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistPlay
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musicapp2026.ui.viewmodel.MusicViewModel
import com.example.musicapp2026.ui.viewmodel.PlaylistUiModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistScreen(
    viewModel: MusicViewModel,
    onPlaylistClick: (PlaylistUiModel) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    onOpenPlayer: () -> Unit
) {
    val playlists by viewModel.playlists.collectAsState()
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.playbackProgress.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }

    val filteredPlaylists = if (searchText.isBlank()) {
        playlists
    } else {
        playlists.filter { it.name.contains(searchText, ignoreCase = true) }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Listas", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = !showMenu }) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            OutlinedTextField(
                                value = searchText,
                                onValueChange = { searchText = it },
                                label = { Text("Search") },
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreatePlaylistClick) {
                Icon(Icons.Default.Add, contentDescription = "Crear Playlist")
            }
        },
        bottomBar = {
            BottomPlayer(
                currentSong = currentSong,
                isPlaying = isPlaying,
                progress = progress,
                onPlayPause = { viewModel.togglePlayPause() },
                onSkipNext = { viewModel.skipNext() },
                onSkipPrevious = { viewModel.skipPrevious() },
                onOpenPlayer = onOpenPlayer
            )
        }
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(paddingValues)
        ) {
            items(filteredPlaylists) { playlist ->
                PlaylistCard(playlist) {
                    onPlaylistClick(playlist)
                }
            }
        }
    }
}

@Composable
fun PlaylistCard(playlist: PlaylistUiModel, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                Icons.AutoMirrored.Filled.PlaylistPlay,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = playlist.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "${playlist.count} canciones",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
