package com.example.musicapp2026.data.repository

import com.example.musicapp2026.data.dao.SongDao
import com.example.musicapp2026.data.datasource.LocalMusicDataSource
import com.example.musicapp2026.data.mapper.toDomain
import com.example.musicapp2026.data.mapper.toEntity
import com.example.musicapp2026.domain.Song
import com.example.musicapp2026.domain.repository.SongRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SongRepositoryImpl @Inject constructor(
    private val dao: SongDao,
    private val localDataSource: LocalMusicDataSource
) : SongRepository {

    override fun observeAllSongs(): Flow<List<Song>> {
        return dao.observeAllSongs().map { entities -> 
            entities.map { it.toDomain() } 
        }
    }

    override suspend fun getAllSongs(): List<Song> {
        return dao.getAllSongs().map { it.toDomain() }
    }

    override suspend fun getSongById(id: Long): Song? {
        return dao.getSongById(id)?.toDomain()
    }

    override suspend fun syncSongs() {
        val deviceSongs = localDataSource.fetchLocalSongs()
        val existingSongs = dao.getAllSongs().associateBy { it.id }

        val songsToInsert = deviceSongs.map { deviceSong ->
            val existing = existingSongs[deviceSong.id]
            if (existing != null) {
                // Preserve user-edited metadata (title, artist, imageUrl) and stats
                // during sync to prevent MediaStore from overwriting them.
                deviceSong.copy(
                    title = existing.title,
                    artist = existing.artist,
                    imageUrl = existing.imageUrl,
                    playCount = existing.playCount,
                    lastPlayed = existing.lastPlayed
                ).toEntity()
            } else {
                deviceSong.toEntity()
            }
        }
        dao.insertSongs(songsToInsert)
    }

    override suspend fun updateSong(song: Song) {
        dao.updateSong(song.toEntity())
    }

    override suspend fun updatePlaybackStats(id: Long) {
        dao.updatePlaybackStats(id, System.currentTimeMillis())
    }

    override suspend fun getRecentlyPlayed(): List<Song> {
        return dao.getRecentlyPlayed().map { it.toDomain() }
    }

    override suspend fun getMostPlayed(): List<Song> {
        return dao.getMostPlayed().map { it.toDomain() }
    }

    override suspend fun insertSongs(songs: List<Song>) {
        dao.insertSongs(songs.map { it.toEntity() })
    }
}
