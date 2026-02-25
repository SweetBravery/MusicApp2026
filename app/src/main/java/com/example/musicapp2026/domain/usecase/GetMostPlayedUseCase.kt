package com.example.musicapp2026.domain.usecase

import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.repository.SongRepository

class GetMostPlayedUseCase(
    private val repository: SongRepository
) {
    suspend operator fun invoke(): List<Song> {
        return repository.getMostPlayed()
    }
}
