package com.example.musicapp2026.playback

import androidx.media3.common.MediaItem

//1.x works independently of domain.Song
class QueueManager {
    //creation of queue as a list of mediaitems
    private val queue = mutableListOf<MediaItem>()

    //queue setter
    fun setQueue(mediaItems: List<MediaItem>) {
        queue.clear()
        queue.addAll(mediaItems)
    }

    //queue getter
    fun getQueue(): List<MediaItem> = queue

    //add mediaitem into the queue
    fun add(mediaItem: MediaItem) {
        queue.add(mediaItem)
    }

    //removes mediaItem from the queue
    fun remove(index: Int) {
        if (index in queue.indices) {
            queue.removeAt(index)
        }
    }

    //clears queue
    fun clear() {
        queue.clear()
    }
}