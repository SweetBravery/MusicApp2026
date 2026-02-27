package com.example.musicapp2026.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.musicapp2026.data.dao.PlaylistDao
import com.example.musicapp2026.data.dao.SongDao

@Database(
    entities = [
        SongEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class
    ],
    version = 5,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract val songDao: SongDao
    abstract val playlistDao: PlaylistDao

    companion object {
        const val DATABASE_NAME = "music_db"
    }
}
