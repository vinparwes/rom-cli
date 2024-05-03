package com.example.rom_cli.data

import android.content.Context
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
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


        fun delete(context: Context, fileName: String) : File? {
            return null;
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
    }
}