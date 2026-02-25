package com.example.musicapp2026.controller

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.musicapp2026.service.MusicService
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors

class MusicController(private val context: Context) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null

    val currentPosition: Long
        get() = controller?.currentPosition ?: 0L

    val duration: Long
        get() = controller?.duration ?: 0L

    fun connect(
        onConnected: () -> Unit,
        onPlaybackStateChanged: (Boolean) -> Unit,
        onMediaItemChanged: (MediaItem?) -> Unit
    ) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        controllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        controllerFuture?.addListener({
            controller = controllerFuture?.get()
            controller?.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    onPlaybackStateChanged(isPlaying)
                }

                override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                    super.onMediaItemTransition(mediaItem, reason)
                    onMediaItemChanged(mediaItem)
                }
            })
            onConnected()
        }, MoreExecutors.directExecutor())
    }

    fun playSong(mediaItem: MediaItem) {
        controller?.setMediaItem(mediaItem)
        controller?.prepare()
        controller?.play()
    }

    fun setPlaylistAndPlay(mediaItems: List<MediaItem>, startIndex: Int) {
        controller?.setMediaItems(mediaItems, startIndex, 0L)
        controller?.prepare()
        controller?.play()
    }

    fun play() {
        controller?.play()
    }

    fun pause() {
        controller?.pause()
    }

    fun skipNext() {
        controller?.seekToNext()
    }

    fun skipPrevious() {
        controller?.seekToPrevious()
    }

    fun seekTo(position: Long) {
        controller?.seekTo(position)
    }

    fun release() {
        controller?.release()
        controller = null
    }
}
