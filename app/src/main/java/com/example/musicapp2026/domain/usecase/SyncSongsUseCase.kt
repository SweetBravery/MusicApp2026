package com.example.musicapp2026.domain.usecase

import com.example.musicapp2026.domain.repository.SongRepository

class SyncSongsUseCase(
    private val repository: SongRepository
) {
    suspend operator fun invoke() {
        repository.syncSongs()
    }
}
