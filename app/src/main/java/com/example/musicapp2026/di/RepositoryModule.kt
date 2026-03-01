package com.example.musicapp2026.di

import com.example.musicapp2026.data.repository.AudioDbRepositoryImpl
import com.example.musicapp2026.data.repository.PlaylistRepositoryImpl
import com.example.musicapp2026.data.repository.SongRepositoryImpl
import com.example.musicapp2026.domain.repository.AudioDbRepository
import com.example.musicapp2026.domain.repository.PlaylistRepository
import com.example.musicapp2026.domain.repository.SongRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSongRepository(
        songRepositoryImpl: SongRepositoryImpl
    ): SongRepository

    @Binds
    @Singleton
    abstract fun bindPlaylistRepository(
        playlistRepositoryImpl: PlaylistRepositoryImpl
    ): PlaylistRepository

    @Binds
    @Singleton
    abstract fun bindAudioDbRepository(
        audioDbRepositoryImpl: AudioDbRepositoryImpl
    ): AudioDbRepository
}
