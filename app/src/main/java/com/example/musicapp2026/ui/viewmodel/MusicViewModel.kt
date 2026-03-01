package com.example.musicapp2026.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.musicapp2026.data.remote.Album
import com.example.musicapp2026.data.remote.Track
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.Playlist
import com.example.musicapp2026.domain.repository.PlaylistRepository
import com.example.musicapp2026.domain.usecase.SearchAudioDbUseCase
import com.example.musicapp2026.service.MusicServiceConnection
import com.example.musicapp2026.util.ImageDownloader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MusicViewModel @Inject constructor(
    private val musicServiceConnection: MusicServiceConnection,
    private val playlistRepository: PlaylistRepository,
    private val searchAudioDbUseCase: SearchAudioDbUseCase,
    private val imageDownloader: ImageDownloader
) : ViewModel() {

    private val _uiState = MutableStateFlow(MusicUiState())
    val uiState: StateFlow<MusicUiState> = _uiState.asStateFlow()

    private val _searchResults = MutableStateFlow<List<Any>>(emptyList())
    val searchResults: StateFlow<List<Any>> = _searchResults.asStateFlow()

    val songs: StateFlow<List<Song>> = musicServiceConnection.allSongs
    val currentSong: StateFlow<Song?> = musicServiceConnection.currentSong
    val isPlaying: StateFlow<Boolean> = musicServiceConnection.isPlaying
    val playbackProgress: StateFlow<Long> = musicServiceConnection.playbackProgress

    val playlists: StateFlow<List<PlaylistUiModel>> = combine(
        playlistRepository.getAllPlaylists(),
        musicServiceConnection.allSongs
    ) { playlists, allSongs ->
        playlists.map { playlist ->
            if (playlist.id == 1L || playlist.title == "Todas las canciones") {
                PlaylistUiModel(
                    id = playlist.id,
                    name = playlist.title,
                    count = allSongs.size,
                    songs = allSongs,
                    imageUrl = playlist.imageUrl
                )
            } else {
                PlaylistUiModel(
                    id = playlist.id,
                    name = playlist.title,
                    count = playlist.songs.size,
                    songs = playlist.songs,
                    imageUrl = playlist.imageUrl
                )
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        observeMusicService()
    }

    private fun observeMusicService() {
        musicServiceConnection.allSongs
            .onEach { songs -> _uiState.update { it.copy(songs = songs) } }
            .launchIn(viewModelScope)

        musicServiceConnection.currentSong
            .onEach { song -> _uiState.update { it.copy(currentSong = song) } }
            .launchIn(viewModelScope)

        musicServiceConnection.isPlaying
            .onEach { isPlaying -> _uiState.update { it.copy(isPlaying = isPlaying) } }
            .launchIn(viewModelScope)

        musicServiceConnection.playbackProgress
            .onEach { progress -> _uiState.update { it.copy(playbackProgress = progress) } }
            .launchIn(viewModelScope)

        musicServiceConnection.repeatMode
            .onEach { mode -> _uiState.update { it.copy(repeatMode = mode) } }
            .launchIn(viewModelScope)

        musicServiceConnection.shuffleModeEnabled
            .onEach { enabled -> _uiState.update { it.copy(isShuffleModeEnabled = enabled) } }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: MusicUiEvent) {
        when (event) {
            is MusicUiEvent.PlaySong -> {
                val startIndex = event.playlist.indexOfFirst { it.id == event.song.id }
                if (startIndex != -1) {
                    musicServiceConnection.playPlaylist(event.playlist, startIndex)
                } else {
                    musicServiceConnection.playSong(event.song)
                }
            }
            MusicUiEvent.TogglePlayPause -> musicServiceConnection.togglePlayPause()
            MusicUiEvent.SkipNext -> musicServiceConnection.skipNext()
            MusicUiEvent.SkipPrevious -> musicServiceConnection.skipPrevious()
            is MusicUiEvent.SeekTo -> musicServiceConnection.seekTo(event.position)
            is MusicUiEvent.UpdateSongImage -> {
                _uiState.value.songs.find { it.id == event.songId }?.let { song ->
                    musicServiceConnection.updateSong(song.copy(imageUrl = event.imageUri))
                }
            }
            is MusicUiEvent.UpdateSongInfo -> {
                _uiState.value.songs.find { it.id == event.songId }?.let { song ->
                    musicServiceConnection.updateSong(song.copy(title = event.title, artist = event.artist))
                }
            }
            MusicUiEvent.ToggleRepeatMode -> musicServiceConnection.toggleRepeatMode()
            MusicUiEvent.ToggleShuffleMode -> musicServiceConnection.toggleShuffleMode()
        }
    }

    fun searchOnline(query: String, isAlbum: Boolean) {
        viewModelScope.launch {
            // Split the query to try and extract artist and title/album
            val parts = query.split(" ", limit = 2)
            val artist = if (parts.size > 1) parts[0] else ""
            val term = if (parts.size > 1) parts[1] else query

            if (isAlbum) {
                // Try searching with split parameters first
                var result = searchAudioDbUseCase.searchAlbum(artist, term)
                // If no result, try searching with everything as the title/album
                if (result == null) {
                    result = searchAudioDbUseCase.searchAlbum("", query)
                }
                _searchResults.value = if (result != null) listOf(result) else emptyList()
            } else {
                // Try searching with split parameters first
                var result = searchAudioDbUseCase.searchTrack(artist, term)
                // If no result, try searching with everything as the track title
                if (result == null) {
                    result = searchAudioDbUseCase.searchTrack("", query)
                }
                _searchResults.value = if (result != null) listOf(result) else emptyList()
            }
        }
    }

    fun downloadAndApplyImage(url: String, targetId: Long, isPlaylist: Boolean) {
        viewModelScope.launch {
            val localPath = imageDownloader.downloadImage(url)
            if (localPath != null) {
                if (isPlaylist) {
                    val playlist = playlists.value.find { it.id == targetId }
                    playlist?.let {
                        updatePlaylist(it.copy(imageUrl = localPath))
                    }
                } else {
                    onEvent(MusicUiEvent.UpdateSongImage(targetId, localPath))
                }
            }
        }
    }

    fun createPlaylist(name: String, songs: List<Song>) {
        viewModelScope.launch {
            playlistRepository.createPlaylistWithSongs(name, songs.map { it.id })
        }
    }

    fun updatePlaylist(playlistUiModel: PlaylistUiModel) {
        viewModelScope.launch {
            playlistRepository.updatePlaylist(Playlist(
                id = playlistUiModel.id,
                title = playlistUiModel.name,
                songs = playlistUiModel.songs,
                imageUrl = playlistUiModel.imageUrl
            ))
        }
    }

    // Traditional helper methods for Compose to avoid excessive event wrapping if preferred
    fun playSong(song: Song, playlist: List<Song>) = onEvent(MusicUiEvent.PlaySong(song, playlist))
    fun togglePlayPause() = onEvent(MusicUiEvent.TogglePlayPause)
    fun skipNext() = onEvent(MusicUiEvent.SkipNext)
    fun skipPrevious() = onEvent(MusicUiEvent.SkipPrevious)
    fun seekTo(position: Long) = onEvent(MusicUiEvent.SeekTo(position))
    fun updateSongImage(songId: Long, uri: String) = onEvent(MusicUiEvent.UpdateSongImage(songId, uri))
}
