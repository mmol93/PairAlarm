package com.easyo.pairalarm.repository

import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.database.table.AlarmData

class AlarmRepository(private val dao: AlarmDAO) {

    fun getAllAlarm() = dao.getAllAlarms()

    suspend fun insertAlarmData(alarmData: AlarmData) {
        dao.insertNewAlarm(alarmData)
    }

    suspend fun updateAlarmData(alarmData: AlarmData) {
        dao.updateAlarm(alarmData)
    }

    suspend fun deleteAlarmData(alarmData: AlarmData) {
        dao.deleteAlarm(alarmData)
    }

    fun searchWithAlarmCode(requestCode: String) =
        dao.searchAlarmDataWithAlarmCode(requestCode)
}
