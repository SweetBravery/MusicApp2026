package com.example.musicapp2026.domain.repository

import com.example.musicapp2026.domain.Playlist

interface PlaylistRepository {
    suspend fun getPlaylistWithSongs(id: Long): Playlist
}