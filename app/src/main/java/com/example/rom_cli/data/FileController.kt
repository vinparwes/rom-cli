package com.example.rom_cli.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaScannerConnection
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class FileController {

    companion object {

        fun saveImageToGallery(context: Context, bitmap: Bitmap, title: String) : Boolean {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, "$title.jpg")
            if(saveBitmapToFile(bitmap, image)) {
                MediaScannerConnection.scanFile(context, arrayOf(image.toString()), arrayOf("image/jpeg"), null)
                return true
            }
            return false
        }

        private fun saveBitmapToFile(bitmap: Bitmap, imageFile: File) : Boolean {
            return try {
                imageFile.createNewFile()
                val outputStream: OutputStream = FileOutputStream(imageFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.flush()
                outputStream.close()
                true
            } catch (e: Exception) {
                false
            }
        }

        fun saveSessionImage(context: Context, bitmap: Bitmap, sessionId: String, suffix: String): String? {
            val dir = File(context.filesDir, "sessions").apply { mkdirs() }
            val file = File(dir, "${sessionId}_${suffix}.jpg")
            return if (saveBitmapToFile(bitmap, file)) "sessions/${sessionId}_${suffix}.jpg" else null
        }

        fun loadSessionImage(context: Context, relativePath: String): Bitmap? {
            val file = File(context.filesDir, relativePath)
            if (!file.exists()) return null
            return BitmapFactory.decodeFile(file.absolutePath)
        }
        fun deleteSessionImages(context: Context, beforePath: String, afterPath: String) {
            File(context.filesDir, beforePath).delete()
            File(context.filesDir, afterPath).delete()
        }
    }
}
