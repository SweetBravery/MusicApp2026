package com.example.musicapp2026.playback

import android.content.Context
import android.media.browse.MediaBrowser
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicapp2026.domain.Song

class PlayBackManager(context: Context) {

    //1.x obtain application context.
    private val applicationContext = context.applicationContext

    //1.x instance exoplayer.
    val player: ExoPlayer by lazy {
        ExoPlayer.Builder(applicationContext)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(androidx.media3.common.C.AUDIO_CONTENT_TYPE_MUSIC)
                    .setUsage(androidx.media3.common.C.USAGE_MEDIA)
                    .build(),
                true
            )
            .build()
    }

    //1.x core playlist management

    fun setSongs(
        songs: List<Song>,
        startIndex: Int = 0,
        startPositionMs: Long = 0L
    ) {
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setUri(song.uri)
                .setMediaId(song.id.toString())
                .build()
        }

        player.setMediaItems(mediaItems, startIndex, startPositionMs)
        player.prepare()
    }

    //1.x core media controllers encapsulated

    fun play() {
        player.playWhenReady = true
    }
    fun pause() {
        player.playWhenReady = false
    }
    fun stop() {
        player.stop()
    }
    fun seekTo(position: Long) {
        player.seekTo(position)
    }
    fun skipToNext() {
        player.seekToNextMediaItem()
    }
    fun skipToPrevious() {
        player.seekToPreviousMediaItem()
    }
    fun setRepeatMode(repeatMode: Int) {
        player.repeatMode = repeatMode
    }

    fun setShuffle(enabled: Boolean) {
        player.shuffleModeEnabled = enabled
    }

    fun addListener(listener: Player.Listener) {
        player.addListener(listener)
    }

    fun release() {
        player.release()
    }


}