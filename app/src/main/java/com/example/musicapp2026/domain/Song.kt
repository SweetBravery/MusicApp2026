package com.example.musicapp2026.domain

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val playCount: Int = 0,
    val lastPlayed: Long = 0L,
    val imageUrl: String? = null
)
