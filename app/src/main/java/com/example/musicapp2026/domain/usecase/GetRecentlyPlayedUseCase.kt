package com.example.musicapp2026.domain.usecase

import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.repository.SongRepository

class GetRecentlyPlayedUseCase(
    private val repository: SongRepository
) {
    suspend operator fun invoke(): List<Song> {
        return repository.getRecentlyPlayed()
    }
}
