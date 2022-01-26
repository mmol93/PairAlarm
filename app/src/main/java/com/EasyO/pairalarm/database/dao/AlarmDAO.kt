package com.EasyO.pairalarm.database.dao

import androidx.room.*
import com.EasyO.pairalarm.database.table.AlarmData
import kotlinx.coroutines.flow.Flow

@Dao
interface AlarmDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewAlarm(alarmData: AlarmData): Long

    @Update
    suspend fun updateAlarm(alarmData: AlarmData)

    @Delete
    suspend fun deleteAlarm(alarmData: AlarmData)

    @Query("SELECT * FROM alarm_data")
    fun getAllAlarms(): Flow<List<AlarmData>>
}
