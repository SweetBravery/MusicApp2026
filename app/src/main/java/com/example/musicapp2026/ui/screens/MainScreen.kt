package com.example.musicapp2026.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

data class UiSong(
    val title: String,
    val artist: String,
    val duration: String
)

@Composable
fun MainScreen() {

    val songs = listOf(
        UiSong("Song One", "Artist A", "3:45"),
        UiSong("Song Two", "Artist B", "4:10"),
        UiSong("Song Three", "Artist C", "2:58"),
        UiSong("Song Four", "Artist D", "5:01"),
    )

    Scaffold(
        topBar = { TopBar() },
        bottomBar = { BottomPlayer() }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(songs) { song ->
                SongItem(song)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {

    TopAppBar(
        title = {
            Text(
                text = "MusicApp 2026",
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Menu, contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { }) {
                Icon(Icons.Default.Sort, contentDescription = "Sort")
            }
        }
    )
}

@Composable
fun SongItem(song: UiSong) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            Icons.Default.MusicNote,
            contentDescription = null,
            modifier = Modifier.size(40.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, fontWeight = FontWeight.SemiBold)
            Text(song.artist, style = MaterialTheme.typography.bodySmall)
        }

        Text(song.duration)
    }
}

@Composable
fun BottomPlayer() {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(8.dp)
    ) {

        // Progress bar
        LinearProgressIndicator(
            progress = 0.3f,
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Song info
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Current Song", fontWeight = FontWeight.Bold)
            Text("Artist - Album", style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(onClick = { }) {
                Icon(Icons.Default.SkipPrevious, contentDescription = "Previous")
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.PlayArrow, contentDescription = "Play")
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.Pause, contentDescription = "Pause")
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.SkipNext, contentDescription = "Next")
            }

            IconButton(onClick = { }) {
                Icon(Icons.Default.Repeat, contentDescription = "Repeat")
            }
        }
    }
}