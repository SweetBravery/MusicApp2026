package com.example.musicapp2026.data.mapper

import com.example.musicapp2026.data.local.PlaylistWithSongs
import com.example.musicapp2026.domain.Playlist

fun PlaylistWithSongs.toDomain(): Playlist {
    return Playlist(
        id = playlist.playlistId,
        name = playlist.name,
        songs = songs.map { it.toDomain() }
    )
}