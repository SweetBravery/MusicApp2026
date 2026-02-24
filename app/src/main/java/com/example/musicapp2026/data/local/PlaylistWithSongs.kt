package com.example.musicapp2026.data.local

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

//Room loads the playlist and the songs together
data class PlaylistWithSongs(
    @Embedded
    val playlist: PlaylistEntity,

    @Relation(
        parentColumn = "playlistId",
        entityColumn = "id",
        associateBy = Junction(PlaylistSongCrossRef::class)
    )
    val songs: List<SongEntity>
)