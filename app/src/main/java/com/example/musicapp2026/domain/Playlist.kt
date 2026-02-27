package com.example.musicapp2026.domain

data class Playlist(
    val id: Long = 0,
    val title: String,
    val songs: List<Song>,
    val imageUrl: String? = null
)
