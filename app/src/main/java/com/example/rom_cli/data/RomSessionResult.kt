package com.example.rom_cli.data

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.Serializable

class RomSessionResult(beforeImage : ByteArray,
                       afterImage : ByteArray,
                       recordedROM : Int,
                       poseIdentifier : String) : Serializable {

    val beforeImage = beforeImage
    val afterImage = afterImage
    val recordedROM = recordedROM
    val poseIdentifier = poseIdentifier

}