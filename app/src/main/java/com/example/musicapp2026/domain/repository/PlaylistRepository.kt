package com.example.musicapp2026.domain.repository

import com.example.musicapp2026.domain.Playlist
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getAllPlaylists(): Flow<List<Playlist>>
    suspend fun getPlaylistWithSongs(id: Long): Playlist
    suspend fun createDefaultPlaylist()
    suspend fun createPlaylistWithSongs(name: String, songIds: List<Long>)
}
