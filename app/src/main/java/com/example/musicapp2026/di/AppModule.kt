package com.example.musicapp2026.di

import android.content.Context
import androidx.room.Room
import com.example.musicapp2026.controller.MusicController
import com.example.musicapp2026.data.dao.PlaylistDao
import com.example.musicapp2026.data.dao.SongDao
import com.example.musicapp2026.data.datasource.LocalMusicDataSource
import com.example.musicapp2026.data.local.MusicDatabase
import com.example.musicapp2026.domain.repository.PlaylistRepository
import com.example.musicapp2026.domain.repository.SongRepository
import com.example.musicapp2026.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMusicDatabase(@ApplicationContext context: Context): MusicDatabase {
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            MusicDatabase.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideSongDao(db: MusicDatabase): SongDao = db.songDao

    @Provides
    @Singleton
    fun providePlaylistDao(db: MusicDatabase): PlaylistDao = db.playlistDao

    @Provides
    @Singleton
    fun provideLocalMusicDataSource(@ApplicationContext context: Context): LocalMusicDataSource {
        return LocalMusicDataSource(context)
    }

    @Provides
    @Singleton
    fun provideGetAllSongsUseCase(repository: SongRepository): GetAllSongsUseCase {
        return GetAllSongsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetAllPlaylistsUseCase(repository: PlaylistRepository): GetAllPlaylistsUseCase {
        return GetAllPlaylistsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetPlaylistUseCase(repository: PlaylistRepository): GetPlaylistUseCase {
        return GetPlaylistUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideSyncSongsUseCase(repository: SongRepository): SyncSongsUseCase {
        return SyncSongsUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetRecentlyPlayedUseCase(repository: SongRepository): GetRecentlyPlayedUseCase {
        return GetRecentlyPlayedUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideGetMostPlayedUseCase(repository: SongRepository): GetMostPlayedUseCase {
        return GetMostPlayedUseCase(repository)
    }

    @Provides
    @Singleton
    fun provideMusicController(@ApplicationContext context: Context): MusicController {
        return MusicController(context)
    }
}
