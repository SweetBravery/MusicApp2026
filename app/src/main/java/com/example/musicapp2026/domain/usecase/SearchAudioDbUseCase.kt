package com.example.musicapp2026.domain.usecase

import com.example.musicapp2026.domain.repository.AudioDbRepository
import javax.inject.Inject

class SearchAudioDbUseCase @Inject constructor(
    private val repository: AudioDbRepository
) {
    suspend fun searchTrack(artist: String, track: String) = repository.searchTrack(artist, track)
    suspend fun searchAlbum(artist: String, album: String) = repository.searchAlbum(artist, album)
}
