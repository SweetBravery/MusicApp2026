package com.example.musicapp2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.repository.PlaylistRepository
import com.example.musicapp2026.domain.usecase.GetAllPlaylistsUseCase
import com.example.musicapp2026.service.MusicServiceConnection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val getAllPlaylistsUseCase: GetAllPlaylistsUseCase,
    private val playlistRepository: PlaylistRepository
) : ViewModel() {

    val songs: StateFlow<List<Song>> = musicServiceConnection.allSongs
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val playlists: StateFlow<List<PlaylistUiModel>> = getAllPlaylistsUseCase()
        .combine(songs) { playlistList, allSongsList ->
            if (playlistList.isEmpty()) {
                viewModelScope.launch {
                    playlistRepository.createDefaultPlaylist()
                }
            }
            playlistList.map { playlist ->
                PlaylistUiModel(
                    id = playlist.id,
                    name = playlist.name,
                    count = if (playlist.id == 1L) allSongsList.size else playlist.songs.size
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val currentSong: StateFlow<Song?> = musicServiceConnection.currentSong
        .stateIn(viewModelScope, SharingStarted.Lazily, null)

    val isPlaying: StateFlow<Boolean> = musicServiceConnection.isPlaying
        .stateIn(viewModelScope, SharingStarted.Lazily, false)

    val playbackProgress: StateFlow<Long> = musicServiceConnection.playbackProgress
        .stateIn(viewModelScope, SharingStarted.Lazily, 0L)

    fun playSong(song: Song) {
        val allSongsList = songs.value
        val startIndex = allSongsList.indexOfFirst { it.id == song.id }
        if (startIndex != -1) {
            musicServiceConnection.playPlaylist(allSongsList, startIndex)
        } else {
            musicServiceConnection.playSong(song)
        }
    }

    fun togglePlayPause() {
        musicServiceConnection.togglePlayPause()
    }

    fun skipNext() {
        musicServiceConnection.skipNext()
    }

    fun skipPrevious() {
        musicServiceConnection.skipPrevious()
    }

    fun seekTo(position: Long) {
        musicServiceConnection.seekTo(position)
    }
}
