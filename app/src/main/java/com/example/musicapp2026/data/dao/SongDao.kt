package com.example.musicapp2026.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.musicapp2026.data.local.SongEntity

//dao communicates between the repository and the entities
@Dao
interface SongDao {

    @Query("SELECT * FROM songs")
    suspend fun getAllSongs(): List<SongEntity>

    @Query("SELECT * FROM songs WHERE id = :id")
    suspend fun getSongById(id: Long): SongEntity?

    @Query("SELECT * FROM songs ORDER BY lastPlayed DESC LIMIT 30")
    suspend fun getRecentlyPlayed(): List<SongEntity>

    @Query("SELECT * FROM songs ORDER BY playCount DESC LIMIT 100")
    suspend fun getMostPlayed(): List<SongEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSongs(songs: List<SongEntity>)

    @Query("UPDATE songs SET playCount = playCount + 1, lastPlayed = :timestamp WHERE id = :id")
    suspend fun updatePlaybackStats(id: Long, timestamp: Long)
}
