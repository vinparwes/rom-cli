package com.example.rom_cli.data.rom_session

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity
data class RomSessionResult(
    @PrimaryKey val uid : String,
    @ColumnInfo(name = "recording_date") val dateRecorded: String,
    @ColumnInfo(name = "starting_position_image_location") val beforeImage : String,
    @ColumnInfo(name = "ending_position_image_location") val afterImage : String,
    @ColumnInfo(name = "recorded_rom") val recordedROM : Int,
    @ColumnInfo(name = "pose_identifier") val poseIdentifier : String
)