package com.easyo.pairalarm.database.dao

import androidx.room.*
import com.easyo.pairalarm.database.table.AlarmData
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

    @Query("SELECT * FROM alarm_data WHERE alarmCode = :alarmCode")
    fun searchAlarmDataWithAlarmCode(alarmCode : String) : Flow<List<AlarmData>>
}
