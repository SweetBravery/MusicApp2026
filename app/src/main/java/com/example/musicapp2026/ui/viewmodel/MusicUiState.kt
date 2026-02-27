package com.example.musicapp2026.ui.viewmodel

import com.example.musicapp2026.domain.Song

data class MusicUiState(
    val songs: List<Song> = emptyList(),
    val currentSong: Song? = null,
    val isPlaying: Boolean = false,
    val playbackProgress: Long = 0L,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class MusicUiEvent {
    data class PlaySong(val song: Song, val playlist: List<Song>) : MusicUiEvent()
    object TogglePlayPause : MusicUiEvent()
    object SkipNext : MusicUiEvent()
    object SkipPrevious : MusicUiEvent()
    data class SeekTo(val position: Long) : MusicUiEvent()
    data class UpdateSongImage(val songId: Long, val imageUri: String) : MusicUiEvent()
    data class UpdateSongInfo(val songId: Long, val title: String, val artist: String) : MusicUiEvent()
}
