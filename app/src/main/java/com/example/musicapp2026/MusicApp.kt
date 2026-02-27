package com.example.musicapp2026

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.example.musicapp2026.util.MediaArtworkFetcher
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MusicApp : Application(), ImageLoaderFactory {
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .components {
                // Add the custom fetcher at the beginning of the list
                add(MediaArtworkFetcher.Factory(this@MusicApp))
            }
            .crossfade(true)
            .build()
    }
}
