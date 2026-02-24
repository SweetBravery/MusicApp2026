package com.example.musicapp2026.controller

import android.content.ComponentName
import android.content.Context
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.MoreExecutors
import com.example.musicapp2026.service.MusicService

//Music controller implements mediacontroller. It is the une used from activities in order to communicate with the mediaSession
//and thus the player.
class MusicController(private val context: Context) {
    //Creates Music Controller variable es null since it requires synchronization
    private var controller: MediaController? = null

    //function that connexts the Activity to the Controller
    fun connect(onConnected: () -> Unit) {
        val sessionToken = SessionToken(
            context,
            ComponentName(context, MusicService::class.java)
        )

        //Builds the media controller
        val controllerFuture =
            MediaController.Builder(context, sessionToken).buildAsync()

        //sets the listener for all mediacontroller calls an actions
        controllerFuture.addListener({
            controller = controllerFuture.get()
            onConnected()
        }, MoreExecutors.directExecutor())
    }

    //encapsulates some of the playbackmanager basic controlls.
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

    fun release() {
        controller?.release()
        controller = null
    }
}