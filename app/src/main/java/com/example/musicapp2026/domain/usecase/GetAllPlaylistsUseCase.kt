package com.example.musicapp2026.domain.usecase

import com.example.musicapp2026.domain.Playlist
import com.example.musicapp2026.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow

class GetAllPlaylistsUseCase(
    private val repository: PlaylistRepository
) {
    operator fun invoke(): Flow<List<Playlist>> {
        return repository.getAllPlaylists()
    }
}
