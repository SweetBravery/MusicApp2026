package com.example.musicapp2026.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicapp2026.data.local.PlaylistEntity
import com.example.musicapp2026.data.local.PlaylistSongCrossRef
import com.example.musicapp2026.data.local.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Transaction
    @Query("SELECT * FROM playlists")
    fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    suspend fun getPlaylistWithSongs(id: Long): PlaylistWithSongs

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistSongCrossRef(ref: PlaylistSongCrossRef)
}
