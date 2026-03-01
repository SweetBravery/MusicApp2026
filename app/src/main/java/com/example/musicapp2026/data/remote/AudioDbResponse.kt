package com.example.musicapp2026.data.remote

import com.google.gson.annotations.SerializedName

data class TrackResponse(
    @SerializedName("track") val tracks: List<Track>?
)

data class Track(
    @SerializedName("idTrack") val id: String,
    @SerializedName("strTrack") val title: String,
    @SerializedName("strArtist") val artist: String,
    @SerializedName("strTrackThumb") val thumb: String?
)

data class AlbumResponse(
    @SerializedName("album") val albums: List<Album>?
)

data class Album(
    @SerializedName("idAlbum") val id: String,
    @SerializedName("strAlbum") val title: String,
    @SerializedName("strArtist") val artist: String,
    @SerializedName("strAlbumThumb") val thumb: String?
)
