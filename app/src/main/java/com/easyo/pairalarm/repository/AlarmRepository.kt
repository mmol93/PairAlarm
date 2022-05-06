package com.easyo.pairalarm.repository

import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.database.table.AlarmData

class AlarmRepository(private val dao: AlarmDAO) {

    suspend fun insert(alarmData: AlarmData){
        dao.insertNewAlarm(alarmData)
    }

    suspend fun update(alarmData: AlarmData){
        dao.updateAlarm(alarmData)
    }

    suspend fun delete(alarmData: AlarmData){
        dao.deleteAlarm(alarmData)
    }
}