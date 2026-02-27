package com.example.musicapp2026.data.mapper

import com.example.musicapp2026.data.local.SongEntity
import com.example.musicapp2026.domain.Song

//turns database into Songtype
fun SongEntity.toDomain(): Song {
    return Song(
        id = id,
        title = title,
        artist = artist,
        album = album,
        duration = duration,
        uri = uri,
        playCount = playCount,
        lastPlayed = lastPlayed,
        imageUrl = imageUrl
    )
}

fun Song.toEntity(): SongEntity {
    return SongEntity(
        id = this.id,
        title = this.title,
        artist = this.artist,
        album = this.album,
        duration = this.duration,
        uri = this.uri,
        playCount = this.playCount,
        lastPlayed = this.lastPlayed,
        imageUrl = this.imageUrl
    )
}