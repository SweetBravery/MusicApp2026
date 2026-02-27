package com.example.musicapp2026.domain.usecase

import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.repository.SongRepository
import javax.inject.Inject

class UpdateSongUseCase @Inject constructor(
    private val repository: SongRepository
) {
    suspend operator fun invoke(song: Song) {
        repository.updateSong(song)
    }
}
