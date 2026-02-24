package com.example.musicapp2026.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.example.musicapp2026.data.local.PlaylistEntity
import com.example.musicapp2026.data.local.PlaylistSongCrossRef
import com.example.musicapp2026.data.local.PlaylistWithSongs

@Dao
interface PlaylistDao {

    @Transaction
    @Query("SELECT * FROM playlists WHERE playlistId = :id")
    suspend fun getPlaylistWithSongs(id: Long): PlaylistWithSongs

    @Insert
    suspend fun insertPlaylist(playlist: PlaylistEntity)

    @Insert
    suspend fun insertPlaylistSongCrossRef(ref: PlaylistSongCrossRef)
}