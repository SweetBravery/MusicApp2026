package com.example.musicapp2026.domain.usecase

import com.example.musicapp2026.domain.Playlist
import com.example.musicapp2026.domain.repository.PlaylistRepository

class GetPlaylistUseCase(
    private val repository: PlaylistRepository
) {

    suspend operator fun invoke(id: Long): Playlist {
        return repository.getPlaylistWithSongs(id)
    }
}
