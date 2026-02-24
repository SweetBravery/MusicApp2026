package com.example.musicapp2026.domain.repository

import com.example.musicapp2026.domain.Song

interface SongRepository {

    suspend fun getAllSongs(): List<Song>

    suspend fun getSongById(id: Long): Song?

    suspend fun insertSongs(songs: List<Song>)

    suspend fun syncSongs()
}