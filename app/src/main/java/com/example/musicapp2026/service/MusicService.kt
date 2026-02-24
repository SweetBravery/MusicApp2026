package com.example.musicapp2026.service

import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.musicapp2026.playback.PlayBackManager

class MusicService : MediaSessionService() {
    private lateinit var playbackManager: PlayBackManager
    private lateinit var mediaSession: MediaSession

    //overrides the lifecycle on create to start the mediaSessionService
    override fun onCreate() {
        super.onCreate()

        //Manager initialization
        playbackManager = PlayBackManager(this)
        //mediaSession Creation
        mediaSession = MediaSession.Builder(this, playbackManager.player).build()
    }

    //getter for the MediaSession
    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    //clean MediaSession
    override fun onDestroy() {
        mediaSession.release()
        playbackManager.release()
        super.onDestroy()
    }
}
