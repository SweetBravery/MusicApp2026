package com.example.musicapp2026.data.repository

import com.example.musicapp2026.data.dao.PlaylistDao
import com.example.musicapp2026.data.mapper.toDomain
import com.example.musicapp2026.domain.Playlist
import com.example.musicapp2026.domain.repository.PlaylistRepository

class PlaylistRepositoryImpl(
    private val dao: PlaylistDao
) : PlaylistRepository {
    override suspend fun getPlaylistWithSongs(id: Long): Playlist {
        return dao.getPlaylistWithSongs(id).toDomain()
    }
}
