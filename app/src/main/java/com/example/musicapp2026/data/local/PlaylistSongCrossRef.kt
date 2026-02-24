package com.example.musicapp2026.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long
)