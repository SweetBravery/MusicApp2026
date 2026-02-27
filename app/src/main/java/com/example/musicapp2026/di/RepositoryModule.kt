package com.example.musicapp2026.di

import com.example.musicapp2026.data.dao.PlaylistDao
import com.example.musicapp2026.data.dao.SongDao
import com.example.musicapp2026.data.datasource.LocalMusicDataSource
import com.example.musicapp2026.data.repository.PlaylistRepositoryImpl
import com.example.musicapp2026.data.repository.SongRepositoryImpl
import com.example.musicapp2026.domain.repository.PlaylistRepository
import com.example.musicapp2026.domain.repository.SongRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideSongRepository(
        songDao: SongDao,
        localMusicDataSource: LocalMusicDataSource
    ): SongRepository {
        return SongRepositoryImpl(songDao, localMusicDataSource)
    }

    @Provides
    @Singleton
    fun providePlaylistRepository(playlistDao: PlaylistDao): PlaylistRepository {
        return PlaylistRepositoryImpl(playlistDao)
    }
}
