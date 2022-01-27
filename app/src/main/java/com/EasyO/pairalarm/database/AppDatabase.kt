package com.EasyO.pairalarm.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.EasyO.pairalarm.database.dao.AlarmDAO
import com.EasyO.pairalarm.database.table.AlarmData

@Database(entities = [AlarmData::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun alarmDao(): AlarmDAO
}