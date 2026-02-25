package com.example.musicapp2026.data.repository

import com.example.musicapp2026.data.dao.SongDao
import com.example.musicapp2026.data.datasource.LocalMusicDataSource
import com.example.musicapp2026.data.mapper.toDomain
import com.example.musicapp2026.data.mapper.toEntity
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.repository.SongRepository

//implementation of the SongRepository interface in domain.repository.SongRepository
class SongRepositoryImpl(
    private val dao: SongDao,
    private val localDataSource: LocalMusicDataSource
) : SongRepository {

    override suspend fun getAllSongs(): List<Song> {
        return dao.getAllSongs().map { it.toDomain() }
    }

    override suspend fun getSongById(id: Long): Song? {
        return dao.getSongById(id)?.toDomain()
    }

    override suspend fun getRecentlyPlayed(): List<Song> {
        return dao.getRecentlyPlayed().map { it.toDomain() }
    }

    override suspend fun getMostPlayed(): List<Song> {
        return dao.getMostPlayed().map { it.toDomain() }
    }

    override suspend fun insertSongs(songs: List<Song>) {
        dao.insertSongs(
            songs.map { it.toEntity() }
        )
    }

    override suspend fun syncSongs() {
        val localSongs = localDataSource.fetchLocalSongs()
        insertSongs(localSongs)
    }

    override suspend fun updatePlaybackStats(id: Long) {
        dao.updatePlaybackStats(id, System.currentTimeMillis())
    }
}
