package com.example.musicapp2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.example.musicapp2026.controller.MusicController
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.usecase.GetAllSongsUseCase
import com.example.musicapp2026.domain.usecase.SyncSongsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val getAllSongsUseCase: GetAllSongsUseCase,
    private val syncSongsUseCase: SyncSongsUseCase,
    private val musicController: MusicController
) : ViewModel() {

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs = _songs.asStateFlow()

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying = _isPlaying.asStateFlow()

    init {
        musicController.connect(
            onConnected = { /* Handle connection */ },
            onPlaybackStateChanged = { isPlaying ->
                _isPlaying.value = isPlaying
            }
        )
        loadMusic()
    }

    private fun loadMusic() {
        viewModelScope.launch {
            syncSongsUseCase()
            _songs.value = getAllSongsUseCase()
        }
    }

    fun playSong(song: Song) {
        _currentSong.value = song
        val mediaItem = MediaItem.Builder()
            .setMediaId(song.id.toString())
            .setUri(song.uri)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(song.title)
                    .setArtist(song.artist)
                    .setAlbumTitle(song.album)
                    .build()
            )
            .build()
        musicController.playSong(mediaItem)
    }

    fun togglePlayPause() {
        if (_isPlaying.value) {
            musicController.pause()
        } else {
            musicController.play()
        }
    }

    fun skipNext() = musicController.skipNext()
    fun skipPrevious() = musicController.skipPrevious()

    override fun onCleared() {
        super.onCleared()
        musicController.release()
    }
}
