package com.example.musicapp2026.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.musicapp2026.ui.viewmodel.MusicViewModel
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongDetailScreen(
    viewModel: MusicViewModel,
    onBack: () -> Unit
) {
    val currentSong by viewModel.currentSong.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val progress by viewModel.playbackProgress.collectAsState()

    val duration = currentSong?.duration ?: 0L
    
    // Internal state for slider during dragging
    var sliderPosition by remember { mutableStateOf<Float?>(null) }
    
    val currentDisplayPosition = sliderPosition ?: progress.toFloat()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ahora suena") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Placeholder for Album Art
            Card(
                modifier = Modifier
                    .size(300.dp)
                    .aspectRatio(1f),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        Icons.Default.MusicNote,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = currentSong?.title ?: "Unknown Title",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = currentSong?.artist ?: "Unknown Artist",
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Column(modifier = Modifier.fillMaxWidth()) {
                Slider(
                    value = currentDisplayPosition.coerceIn(0f, duration.coerceAtLeast(1L).toFloat()),
                    onValueChange = { sliderPosition = it },
                    onValueChangeFinished = {
                        sliderPosition?.let {
                            viewModel.seekTo(it.toLong())
                        }
                        sliderPosition = null
                    },
                    valueRange = 0f..duration.coerceAtLeast(1L).toFloat(),
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(formatTime(currentDisplayPosition.toLong()), style = MaterialTheme.typography.bodySmall)
                    Text(formatTime(duration), style = MaterialTheme.typography.bodySmall)
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { viewModel.skipPrevious() }) {
                    Icon(Icons.Default.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(48.dp))
                }
                
                LargeFloatingActionButton(
                    onClick = { viewModel.togglePlayPause() },
                    shape = MaterialTheme.shapes.extraLarge
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(48.dp)
                    )
                }

                IconButton(onClick = { viewModel.skipNext() }) {
                    Icon(Icons.Default.SkipNext, contentDescription = "Next", modifier = Modifier.size(48.dp))
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
