package com.example.musicapp2026.data.repository

import com.example.musicapp2026.data.remote.Album
import com.example.musicapp2026.data.remote.AudioDbService
import com.example.musicapp2026.data.remote.Track
import com.example.musicapp2026.domain.repository.AudioDbRepository
import javax.inject.Inject

class AudioDbRepositoryImpl @Inject constructor(
    private val api: AudioDbService
) : AudioDbRepository {
    override suspend fun searchTrack(artist: String, track: String): Track? {
        return try {
            api.searchTrack(artist, track).tracks?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun searchAlbum(artist: String, album: String): Album? {
        return try {
            api.searchAlbum(artist, album).albums?.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }
}
