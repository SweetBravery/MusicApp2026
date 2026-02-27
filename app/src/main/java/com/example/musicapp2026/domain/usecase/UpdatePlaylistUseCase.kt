package com.example.musicapp2026.domain.usecase

import com.example.musicapp2026.domain.Playlist
import com.example.musicapp2026.domain.repository.PlaylistRepository
import javax.inject.Inject

class UpdatePlaylistUseCase @Inject constructor(
    private val repository: PlaylistRepository
) {
    suspend operator fun invoke(playlist: Playlist) {
        repository.updatePlaylist(playlist)
    }
}
