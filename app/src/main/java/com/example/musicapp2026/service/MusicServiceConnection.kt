package com.example.musicapp2026.service

import android.content.Context
import androidx.media3.common.MediaItem
import com.example.musicapp2026.controller.MusicController
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.usecase.GetAllSongsUseCase
import com.example.musicapp2026.domain.usecase.SyncSongsUseCase
import com.example.musicapp2026.domain.usecase.UpdateSongUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MusicServiceConnection @Inject constructor(
    @ApplicationContext private val context: Context,
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val syncSongsUseCase: SyncSongsUseCase,
    private val updateSongUseCase: UpdateSongUseCase,
    private val musicController: MusicController
) {
    private val _allSongs = MutableStateFlow<List<Song>>(emptyList())
    val allSongs: StateFlow<List<Song>> = _allSongs

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying

    private val _playbackProgress = MutableStateFlow(0L)
    val playbackProgress: StateFlow<Long> = _playbackProgress

    private val connectionScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    init {
        connectionScope.launch {
            syncSongsUseCase()
            loadSongs()
        }
        
        musicController.connect(
            onConnected = {
                // Connection established
            },
            onPlaybackStateChanged = { isPlaying ->
                _isPlaying.value = isPlaying
            },
            onMediaItemChanged = { mediaItem ->
                _currentSong.value = _allSongs.value.find { it.id.toString() == mediaItem?.mediaId }
            }
        )

        connectionScope.launch {
            while (true) {
                if (_isPlaying.value) {
                    _playbackProgress.value = musicController.currentPosition
                }
                delay(1000)
            }
        }
    }

    private fun loadSongs() {
        connectionScope.launch {
            _allSongs.value = getAllSongsUseCase()
        }
    }

    fun playPlaylist(songs: List<Song>, startIndex: Int) {
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setMediaId(song.id.toString())
                .setUri(song.uri)
                .build()
        }
        musicController.setPlaylistAndPlay(mediaItems, startIndex)
    }

    fun playSong(song: Song) {
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .build()
        musicController.playSong(mediaItem)
    }

    fun togglePlayPause() {
        if (_isPlaying.value) musicController.pause() else musicController.play()
    }

    fun skipNext() = musicController.skipNext()
    fun skipPrevious() = musicController.skipPrevious()
    fun seekTo(position: Long) = musicController.seekTo(position)

    fun updateSong(song: Song) {
        connectionScope.launch {
            updateSongUseCase(song)
            loadSongs() // Refresh the list
        }
    }
}
