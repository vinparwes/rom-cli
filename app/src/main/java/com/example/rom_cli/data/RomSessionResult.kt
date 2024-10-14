package com.example.rom_cli.data

import java.io.Serializable

class RomSessionResult(dateRecorded: String,
                       dateTimeHash: String,
                       beforeImage : ByteArray,
                       afterImage : ByteArray,
                       recordedROM : Int,
                       poseIdentifier : String) : Serializable
{
    val dateRecorded = dateRecorded
    val dateTimeHash = dateTimeHash
    val beforeImage = beforeImage
    val afterImage = afterImage
    val recordedROM = recordedROM
    val poseIdentifier = poseIdentifier
}