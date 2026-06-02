package com.example.rom_cli.data.rom_session

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RomSessionDao {
    @Insert
    suspend fun insert(session: RomSessionResult)
    @Query("SELECT * FROM RomSessionResult ORDER BY recording_date DESC")
    suspend fun getAll(): List<RomSessionResult>
    @Query("SELECT * FROM RomSessionResult WHERE uid = :id LIMIT 1")
    suspend fun getById(id: String): RomSessionResult?
    @Delete
    suspend fun delete(session: RomSessionResult)
}