package com.example.musicapp2026.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.ui.viewmodel.MusicUiEvent
import com.example.musicapp2026.ui.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: MusicViewModel,
    title: String = "Todas las canciones",
    songs: List<Song>? = null,
    onBack: () -> Unit,
    onOpenPlayer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var songForMenu by remember { mutableStateOf<Song?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var songToEdit by remember { mutableStateOf<Song?>(null) }

    val context = LocalContext.current

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                songForMenu?.let {
                    viewModel.onEvent(MusicUiEvent.UpdateSongImage(it.id, uri.toString()))
                }
            }
        }
    )

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }
    )

    val displaySongs = songs ?: uiState.songs

    val filteredSongs = if (searchText.isBlank()) {
        displaySongs
    } else {
        displaySongs.filter {
            it.title.contains(searchText, ignoreCase = true) ||
                    it.artist.contains(searchText, ignoreCase = true)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
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
        bottomBar = {
            BottomPlayer(
                currentSong = uiState.currentSong,
                isPlaying = uiState.isPlaying,
                progress = uiState.playbackProgress,
                onPlayPause = { viewModel.onEvent(MusicUiEvent.TogglePlayPause) },
                onSkipNext = { viewModel.onEvent(MusicUiEvent.SkipNext) },
                onSkipPrevious = { viewModel.onEvent(MusicUiEvent.SkipPrevious) },
                onOpenPlayer = onOpenPlayer
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(filteredSongs, key = { it.id }) { song ->
                Box {
                    SongItem(
                        song = song,
                        modifier = Modifier.combinedClickable(
                            onClick = { viewModel.onEvent(MusicUiEvent.PlaySong(song, displaySongs)) },
                            onLongClick = { songForMenu = song }
                        )
                    )
                    DropdownMenu(
                        expanded = songForMenu == song,
                        onDismissRequest = { songForMenu = null }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Edit Info") },
                            onClick = {
                                songToEdit = song
                                showEditDialog = true
                                songForMenu = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Edit Image") },
                            onClick = {
                                songForMenu = song // Re-ensure it's set
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                                }
                                // We don't null songForMenu here because imagePicker needs it
                            }
                        )
                    }
                }
            }
        }
    }
    
    if (showEditDialog && songToEdit != null) {
        EditSongDialog(
            song = songToEdit!!,
            onDismiss = { showEditDialog = false },
            onSave = { id, title, artist ->
                viewModel.onEvent(MusicUiEvent.UpdateSongInfo(id, title, artist))
                showEditDialog = false
            }
        )
    }
}

@Composable
fun EditSongDialog(
    song: Song,
    onDismiss: () -> Unit,
    onSave: (Long, String, String) -> Unit
) {
    var title by remember { mutableStateOf(song.title) }
    var artist by remember { mutableStateOf(song.artist) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Song Info") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") }
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = artist,
                    onValueChange = { artist = it },
                    label = { Text("Artist") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSave(song.id, title, artist)
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun SongItem(song: Song, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp)),
            contentScale = ContentScale.Crop,
            error = rememberVectorPainter(Icons.Default.MusicNote),
            placeholder = rememberVectorPainter(Icons.Default.MusicNote)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = song.title, fontWeight = FontWeight.Bold)
            Text(text = song.artist, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun BottomPlayer(
    currentSong: Song?,
    isPlaying: Boolean,
    progress: Long,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onOpenPlayer: () -> Unit
) {
    val duration = currentSong?.duration ?: 1L
    val progressFraction = (progress.toFloat() / duration.toFloat()).coerceIn(0f, 1f)

    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = currentSong != null, onClick = onOpenPlayer)
    ) {
        Column(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
        ) {
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = currentSong?.title ?: "No se está reproduciendo nada",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currentSong?.artist ?: "Selecciona una canción",
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onSkipPrevious,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        modifier = Modifier.size(36.dp)
                    )
                }

                FilledIconButton(
                    onClick = onPlayPause,
                    modifier = Modifier.size(72.dp),
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(
                        if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play/Pause",
                        modifier = Modifier.size(44.dp)
                    )
                }

                IconButton(
                    onClick = onSkipNext,
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        Icons.Default.SkipNext,
                        contentDescription = "Next",
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
        }
    }
}
