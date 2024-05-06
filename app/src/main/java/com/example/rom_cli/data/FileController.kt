package com.example.rom_cli.data

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
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

        fun saveImageToStorage(context: Context, image: Bitmap, fileName: String): Boolean {
            Log.i("FILE_CONTROLLER", "@")
            if (PermissionsController.checkStoragePermission(context)) {
                var outputStream: OutputStream? = null
                try {
                    val folder = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                        "rom-cli"
                    )
                    if (!folder.exists()) {
                        folder.mkdirs()
                    }
                    val file = File(folder, fileName)
                    outputStream = FileOutputStream(file)
                    image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()
                    return true
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    try {
                        outputStream?.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            return false
        }
    }
}