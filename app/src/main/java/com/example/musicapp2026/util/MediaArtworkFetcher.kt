package com.example.musicapp2026.util

import android.content.Context
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.core.graphics.drawable.toDrawable
import coil.ImageLoader
import coil.decode.DataSource
import coil.fetch.DrawableResult
import coil.fetch.FetchResult
import coil.fetch.Fetcher
import coil.request.Options
import coil.size.pxOrElse

/**
 * A robust fetcher that extracts artwork from media files (Audio/Video) 
 * or retrieves thumbnails from the system MediaStore.
 */
class MediaArtworkFetcher(
    private val context: Context,
    private val uri: Uri,
    private val options: Options
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        // 1. Try system's loadThumbnail (Works for MediaStore URIs and some Document URIs)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val size = options.size
                val width = size.width.pxOrElse { 512 }
                val height = size.height.pxOrElse { 512 }
                
                val bitmap = context.contentResolver.loadThumbnail(uri, Size(width, height), null)
                if (bitmap != null) {
                    return DrawableResult(
                        drawable = bitmap.toDrawable(context.resources),
                        isSampled = false,
                        dataSource = DataSource.DISK
                    )
                }
            } catch (e: Exception) {
                // Ignore and fall back
            }
        }

        // 2. Try MediaMetadataRetriever (Extracts embedded artwork from MP3/MP4/etc.)
        val mmr = MediaMetadataRetriever()
        try {
            // Check if it's a content URI or a file path
            mmr.setDataSource(context, uri)
            val picture = mmr.embeddedPicture
            if (picture != null) {
                val bitmap = BitmapFactory.decodeByteArray(picture, 0, picture.size)
                if (bitmap != null) {
                    return DrawableResult(
                        drawable = bitmap.toDrawable(context.resources),
                        isSampled = false,
                        dataSource = DataSource.DISK
                    )
                }
            }
        } catch (e: Exception) {
            // If this fails, it's likely a normal image file or we lack permissions
        } finally {
            try {
                mmr.release()
            } catch (e: Exception) {}
        }

        // Return null to let Coil's default image decoders handle it (e.g., if it's a plain JPEG)
        return null
    }

    class Factory(private val context: Context) : Fetcher.Factory<Uri> {
        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? {
            val scheme = data.scheme
            if (scheme != "content" && scheme != "file") return null

            // We handle any URI that isn't obviously a web URL.
            // fetch() will determine if it can actually extract a thumbnail.
            // This is safer than trying to guess by the URI string.
            val mimeType = context.contentResolver.getType(data)
            val isMediaFile = mimeType?.let { 
                it.startsWith("audio/") || it.startsWith("video/") 
            } ?: false

            // If it's a media file, definitely use this fetcher.
            // If it's a content URI from MediaStore (authority contains 'media'), also use it.
            if (isMediaFile || data.authority?.contains("media") == true) {
                return MediaArtworkFetcher(context, data, options)
            }
            
            return null
        }
    }
}
