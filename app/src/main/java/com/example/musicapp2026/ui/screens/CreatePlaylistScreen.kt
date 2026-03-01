package com.example.musicapp2026.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.ui.viewmodel.MusicUiEvent
import com.example.musicapp2026.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit,
    onConfirm: (String, List<Long>) -> Unit,
    onOpenPlayer: () -> Unit
) {
    val songs by viewModel.songs.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    
    var playlistName by remember { mutableStateOf("") }
    val selectedSongs = remember { mutableStateListOf<Long>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nueva Playlist", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { if (playlistName.isNotBlank()) onConfirm(playlistName, selectedSongs.toList()) },
                        enabled = playlistName.isNotBlank() && selectedSongs.isNotEmpty()
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Confirmar")
                    }
                }
            )
        },
        bottomBar = {
            BottomPlayer(
                currentSong = uiState.currentSong,
                isPlaying = uiState.isPlaying,
                progress = uiState.playbackProgress,
                repeatMode = uiState.repeatMode,
                isShuffleEnabled = uiState.isShuffleModeEnabled,
                onPlayPause = { viewModel.onEvent(MusicUiEvent.TogglePlayPause) },
                onSkipNext = { viewModel.onEvent(MusicUiEvent.SkipNext) },
                onSkipPrevious = { viewModel.onEvent(MusicUiEvent.SkipPrevious) },
                onToggleRepeat = { viewModel.onEvent(MusicUiEvent.ToggleRepeatMode) },
                onToggleShuffle = { viewModel.onEvent(MusicUiEvent.ToggleShuffleMode) },
                onOpenPlayer = onOpenPlayer
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = playlistName,
                onValueChange = { playlistName = it },
                label = { Text("Nombre de la playlist") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text("Selecciona canciones", style = MaterialTheme.typography.titleMedium)
            
            LazyColumn(modifier = Modifier.weight(1f)) {
                items(songs) { song ->
                    SelectableSongItem(
                        song = song,
                        isSelected = selectedSongs.contains(song.id),
                        onSelectedChange = { isSelected ->
                            if (isSelected) selectedSongs.add(song.id)
                            else selectedSongs.remove(song.id)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SelectableSongItem(
    song: Song,
    isSelected: Boolean,
    onSelectedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelectedChange(!isSelected) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectedChange
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
            Text(text = song.title, fontWeight = FontWeight.Bold)
            Text(text = song.artist, style = MaterialTheme.typography.bodySmall)
        }
    }
}
