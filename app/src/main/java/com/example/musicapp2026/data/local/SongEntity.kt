package com.example.musicapp2026.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

//sql representation of a song
@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val uri: String,
    val playCount: Int = 0,
    val lastPlayed: Long = 0L,
    val imageUrl: String? = null
)
