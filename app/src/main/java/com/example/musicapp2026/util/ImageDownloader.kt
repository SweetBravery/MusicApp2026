package com.example.musicapp2026.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ImageDownloader(private val context: Context) {
    private val client = OkHttpClient()

    suspend fun downloadImage(url: String): String? = withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            if (!response.isSuccessful) return@withContext null

            val inputStream: InputStream = response.body?.byteStream() ?: return@withContext null
            val bitmap = BitmapFactory.decodeStream(inputStream) ?: return@withContext null

            val fileName = "cover_${UUID.randomUUID()}.jpg"
            val file = File(context.getExternalFilesDir(null), "covers").apply {
                if (!exists()) mkdirs()
            }
            val destinationFile = File(file, fileName)

            FileOutputStream(destinationFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }

            return@withContext destinationFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
