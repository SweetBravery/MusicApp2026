package com.example.musicapp2026.domain.repository

import com.example.musicapp2026.data.remote.Album
import com.example.musicapp2026.data.remote.Track

interface AudioDbRepository {
    suspend fun searchTrack(artist: String, track: String): Track?
    suspend fun searchAlbum(artist: String, album: String): Album?
}
