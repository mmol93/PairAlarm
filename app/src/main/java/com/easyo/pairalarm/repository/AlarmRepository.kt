package com.easyo.pairalarm.repository

import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.database.table.AlarmData

class AlarmRepository(private val dao: AlarmDAO):AlarmInterface {

    override fun getAllAlarm() = dao.getAllAlarms()

    override suspend fun insertAlarmData(alarmData: AlarmData) {
        dao.insertNewAlarm(alarmData)
    }

    override suspend fun updateAlarmData(alarmData: AlarmData) {
        dao.updateAlarm(alarmData)
    }

    override suspend fun deleteAlarmData(alarmData: AlarmData) {
        dao.deleteAlarm(alarmData)
    }

    override fun searchWithAlarmCode(alarmCode: String) =
        dao.searchAlarmDataWithAlarmCode(alarmCode)
}
