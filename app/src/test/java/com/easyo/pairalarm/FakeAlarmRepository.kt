package com.easyo.pairalarm

import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.repository.AlarmInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf

class FakeAlarmRepository : AlarmInterface {
    private val alarms = MutableStateFlow(mutableListOf<AlarmData>())

    override fun getAllAlarm(): Flow<List<AlarmData>> {
        return alarms
    }

    override suspend fun insertAlarmData(alarmData: AlarmData) {
        alarms.value.add(alarmData)
    }

    override suspend fun updateAlarmData(alarmData: AlarmData) {
        if (alarms.value.indexOf(alarmData) != -1) {
            alarms.value.let {
                it[it.indexOf(alarmData)] = alarmData
            }
        }
    }

    override suspend fun deleteAlarmData(alarmData: AlarmData) {
        alarms.value.remove(alarmData)
    }

    override fun searchWithAlarmCode(alarmCode: String): Flow<AlarmData> {
        return if (alarms.value.none { it.alarmCode == alarmCode }) {
            emptyFlow()
        } else {
            // use first method because alarmCode is unique.
            flowOf(alarms.value.first { it.alarmCode == alarmCode })
        }
    }
}