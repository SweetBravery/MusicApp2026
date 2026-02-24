package com.example.musicapp2026.playback

import android.content.Context
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class MediaSessionManager(context: Context, playBackManager: PlayBackManager) {
    //MediaSession creation
    private val mediaSession: MediaSession = MediaSession.Builder(
        context,
        playBackManager.player
    ).build()

    //mediaSession getter
    fun getSession(): MediaSession = mediaSession

    //release the mediaSession resources
    fun release() {
        mediaSession.release()
    }
}