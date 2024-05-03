package com.example.rom_cli.data

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

class Utility {

    companion object {
        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            return stream.toByteArray()
        }
    }
}