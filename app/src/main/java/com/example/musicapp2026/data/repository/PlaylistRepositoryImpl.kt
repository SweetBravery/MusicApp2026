package com.example.musicapp2026.data.repository

import com.example.musicapp2026.data.dao.PlaylistDao
import com.example.musicapp2026.data.local.PlaylistEntity
import com.example.musicapp2026.data.local.PlaylistSongCrossRef
import com.example.musicapp2026.data.mapper.toDomain
import com.example.musicapp2026.domain.Playlist
import com.example.musicapp2026.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PlaylistRepositoryImpl @Inject constructor(
    private val dao: PlaylistDao
) : PlaylistRepository {
    override fun getAllPlaylists(): Flow<List<Playlist>> {
        return dao.getAllPlaylistsWithSongs().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getPlaylistWithSongs(id: Long): Playlist {
        return dao.getPlaylistWithSongs(id).toDomain()
    }

    override suspend fun createDefaultPlaylist() {
        dao.insertPlaylist(PlaylistEntity(1L, "Todas las canciones"))
    }

    override suspend fun createPlaylistWithSongs(name: String, songIds: List<Long>) {
        val playlistId = System.currentTimeMillis()
        dao.insertPlaylist(PlaylistEntity(playlistId, name))
        songIds.forEach { songId ->
            dao.insertPlaylistSongCrossRef(PlaylistSongCrossRef(playlistId, songId))
        }
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        dao.updatePlaylist(PlaylistEntity(
            playlistId = playlist.id,
            name = playlist.title,
            imageUrl = playlist.imageUrl
        ))
    }
}
