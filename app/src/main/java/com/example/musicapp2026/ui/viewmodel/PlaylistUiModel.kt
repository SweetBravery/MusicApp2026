package com.example.musicapp2026.ui.viewmodel

import com.example.musicapp2026.domain.Song

data class PlaylistUiModel(
    val id: Long,
    val name: String,
    val count: Int,
    val songs: List<Song> = emptyList()
)
