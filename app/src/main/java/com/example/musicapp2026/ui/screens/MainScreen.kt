package com.example.musicapp2026.ui.screens

import android.Manifest
import android.content.Intent
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.musicapp2026.data.remote.Track
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.ui.viewmodel.MusicUiEvent
import com.example.musicapp2026.ui.viewmodel.MusicViewModel
import androidx.media3.common.Player

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MainScreen(
    viewModel: MusicViewModel,
    title: String = "Todas las canciones",
    songs: List<Song>? = null,
    onBack: () -> Unit,
    onMenuClick: () -> Unit,
    onOpenPlayer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    
    var showMenu by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    var songForMenu by remember { mutableStateOf<Song?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var showOnlineSearchDialog by remember { mutableStateOf(false) }
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
                songToEdit?.let {
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
                    Row {
                        IconButton(onClick = onMenuClick) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
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
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            items(filteredSongs, key = { it.id }) { song ->
                val isCurrentSong = uiState.currentSong?.id == song.id
                Box {
                    SongItem(
                        song = song,
                        isCurrentSong = isCurrentSong,
                        isPlaying = uiState.isPlaying && isCurrentSong,
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
                            text = { Text("Editar Info") },
                            onClick = {
                                songToEdit = song
                                showEditDialog = true
                                songForMenu = null
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Editar Imagen") },
                            onClick = {
                                songToEdit = song
                                showImageSourceDialog = true
                                songForMenu = null
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

    if (showImageSourceDialog && songToEdit != null) {
        ImageSourceDialog(
            onDismiss = { showImageSourceDialog = false },
            onSelectFromDevice = {
                showImageSourceDialog = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                } else {
                    permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            },
            onSearchOnline = {
                showImageSourceDialog = false
                showOnlineSearchDialog = true
            }
        )
    }

    if (showOnlineSearchDialog && songToEdit != null) {
        OnlineSearchDialog(
            initialQuery = "${songToEdit!!.artist} ${songToEdit!!.title}",
            searchResults = searchResults,
            onSearch = { query -> viewModel.searchOnline(query, isAlbum = false) },
            onSelect = { url ->
                viewModel.downloadAndApplyImage(url, songToEdit!!.id, isPlaylist = false)
                showOnlineSearchDialog = false
            },
            onDismiss = { showOnlineSearchDialog = false }
        )
    }
}

@Composable
fun ImageSourceDialog(
    onDismiss: () -> Unit,
    onSelectFromDevice: () -> Unit,
    onSearchOnline: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar origen de imagen") },
        text = {
            Column {
                TextButton(
                    onClick = onSelectFromDevice,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Seleccionar desde el dispositivo")
                    }
                }
                TextButton(
                    onClick = onSearchOnline,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Public, contentDescription = null)
                        Spacer(Modifier.width(12.dp))
                        Text("Buscar con API theaudioDB")
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun OnlineSearchDialog(
    initialQuery: String,
    searchResults: List<Any>,
    onSearch: (String) -> Unit,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var query by remember { mutableStateOf(initialQuery) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buscar en línea") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    label = { Text("Término de búsqueda") },
                    trailingIcon = {
                        IconButton(onClick = { onSearch(query) }) {
                            Icon(Icons.Default.Search, contentDescription = null)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))
                LazyColumn(modifier = Modifier.heightIn(max = 300.dp)) {
                    items(searchResults) { result ->
                        val (title, artist, thumb) = when (result) {
                            is Track -> Triple(result.title, result.artist, result.thumb)
                            is com.example.musicapp2026.data.remote.Album -> Triple(result.title, result.artist, result.thumb)
                            else -> Triple("", "", null)
                        }
                        
                        if (thumb != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onSelect(thumb) }
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                AsyncImage(
                                    model = thumb,
                                    contentDescription = null,
                                    modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text(artist, style = MaterialTheme.typography.bodySmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
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
fun SongItem(
    song: Song, 
    isCurrentSong: Boolean = false,
    isPlaying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val textColor = if (isCurrentSong) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
    
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(contentAlignment = Alignment.Center) {
            AsyncImage(
                model = song.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = rememberVectorPainter(Icons.Default.MusicNote),
                placeholder = rememberVectorPainter(Icons.Default.MusicNote)
            )
            if (isPlaying) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center
                ) {
                    MusicVisualizerAnimation()
                }
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title, 
                fontWeight = FontWeight.Bold,
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist, 
                style = MaterialTheme.typography.bodyMedium,
                color = if (isCurrentSong) textColor.copy(alpha = 0.8f) else MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (isCurrentSong && !isPlaying) {
            Icon(
                Icons.Default.Pause,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun MusicVisualizerAnimation() {
    val infiniteTransition = rememberInfiniteTransition(label = "visualizer")
    
    @Composable
    fun Bar(durationMillis: Int) {
        val height by infiniteTransition.animateFloat(
            initialValue = 0.2f,
            targetValue = 0.8f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            ),
            label = "barHeight"
        )
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight(height)
                .background(Color(0xFFFFD700), RoundedCornerShape(2.dp))
        )
    }

    Row(
        modifier = Modifier.height(28.dp),
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        Bar(400)
        Bar(600)
        Bar(500)
        Bar(700)
    }
}

@Composable
fun BottomPlayer(
    currentSong: Song?,
    isPlaying: Boolean,
    progress: Long,
    repeatMode: Int,
    isShuffleEnabled: Boolean,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleShuffle: () -> Unit,
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
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Left: Equalizer Placeholder
                IconButton(onClick = { /* Placeholder */ }) {
                    Icon(
                        Icons.Default.GraphicEq,
                        contentDescription = "Equalizer",
                        modifier = Modifier.size(28.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    IconButton(
                        onClick = onSkipPrevious,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipPrevious,
                            contentDescription = "Previous",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    FilledIconButton(
                        onClick = onPlayPause,
                        modifier = Modifier.size(64.dp),
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            modifier = Modifier.size(40.dp)
                        )
                    }

                    IconButton(
                        onClick = onSkipNext,
                        modifier = Modifier.size(48.dp)
                    ) {
                        Icon(
                            Icons.Default.SkipNext,
                            contentDescription = "Next",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Right: Shuffle/Repeat Combined
                IconButton(
                    onClick = {
                        if (isShuffleEnabled) {
                            onToggleShuffle() // Disable shuffle and keep repeat mode
                        } else {
                            onToggleRepeat() // Cycle repeat modes
                            if (repeatMode == Player.REPEAT_MODE_OFF) {
                                onToggleShuffle() // Enable shuffle if repeat cycles back to off
                            }
                        }
                    },
                    modifier = Modifier.size(48.dp)
                ) {
                    val icon = when {
                        isShuffleEnabled -> Icons.Default.Shuffle
                        repeatMode == Player.REPEAT_MODE_ONE -> Icons.Default.RepeatOne
                        repeatMode == Player.REPEAT_MODE_ALL -> Icons.Default.Repeat
                        else -> Icons.Default.Repeat // Default icon when everything is off
                    }
                    val tint = if (isShuffleEnabled || repeatMode != Player.REPEAT_MODE_OFF) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                    
                    Icon(
                        icon,
                        contentDescription = "Playback Mode",
                        modifier = Modifier.size(28.dp),
                        tint = tint
                    )
                }
            }
        }
    }
}
