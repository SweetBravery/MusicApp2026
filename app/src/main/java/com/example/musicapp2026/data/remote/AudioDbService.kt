package com.example.musicapp2026.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface AudioDbService {
    @GET("searchtrack.php")
    suspend fun searchTrack(
        @Query("s") artist: String,
        @Query("t") track: String
    ): TrackResponse

    @GET("searchalbum.php")
    suspend fun searchAlbum(
        @Query("s") artist: String,
        @Query("a") album: String
    ): AlbumResponse

    companion object {
        const val BASE_URL = "https://www.theaudiodb.com/api/v1/json/2/" // Public test key is '2'
    }
}
