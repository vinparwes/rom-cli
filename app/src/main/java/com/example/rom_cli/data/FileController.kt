package com.example.rom_cli.data

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.OutputStream

class FileController {

    companion object {
        fun getRomResult(context: Context, fileName : String) : RomSessionResult? {
            val file = File(context.filesDir, fileName)
            try {
                val ois = ObjectInputStream(FileInputStream(file))
                val obj = ois.readObject() as RomSessionResult
                ois.close()
                return obj
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
            return null
        }

        fun getRomResults(context: Context) : List<RomSessionResult>? {
            val fileList = context.fileList()
            val files : MutableList<String> = ArrayList()
            for (string in fileList) {
                files.add(string)
            }
            if(files == null || files.isEmpty()) {
                return null
            }
            files.removeFirst()
            //TODO(Need to disregard the file profileinstalled. Possible through foldering?)

            val list : MutableList<RomSessionResult> = ArrayList()
            for (fileName in files) {
                val file = File(context.filesDir, fileName)
                try {
                    val ois = ObjectInputStream(FileInputStream(file))
                    val obj = ois.readObject() as RomSessionResult
                    list.add(obj)
                    ois.close()
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }
            }
            return list
        }


        fun delete(context: Context, fileName: String) : Boolean {
            val dir: File = context.filesDir
            val file = File(dir, fileName)
            return file.delete()
        }

        fun putObject(obj : Any, context : Context, fileName: String) : Boolean {
            try {
                val path = File(context.filesDir, fileName)
                ObjectOutputStream(FileOutputStream(path)).use { outputStream ->
                    outputStream.writeObject(obj)
                }
                return true
            } catch (e : Exception) {
                e.printStackTrace()
                return false
            }
        }

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
    }
}