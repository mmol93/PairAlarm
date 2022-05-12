package com.easyo.pairalarm.util

import com.easyo.pairalarm.database.table.AlarmData

fun getNextAlarm(alarmList: List<AlarmData>): String? {
    val milliSecondForAlarmList = mutableListOf<Long>()
    alarmList.forEach { alarmData ->
        if (alarmData.button){
            if (alarmData.Sun) {
                milliSecondForAlarmList.add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 1))
            }
            if (alarmData.Mon) {
                milliSecondForAlarmList.add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 2))
            }
            if (alarmData.Tue) {
                milliSecondForAlarmList.add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 3))
            }
            if (alarmData.Wed) {
                milliSecondForAlarmList.add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 4))
            }
            if (alarmData.Thu) {
                milliSecondForAlarmList.add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 5))
            }
            if (alarmData.Fri) {
                milliSecondForAlarmList.add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 6))
            }
            if (alarmData.Sat) {
                milliSecondForAlarmList.add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 7))
            }
        }
    }
    return milliSecondForAlarmList.minOrNull()?.let { transMillisToTime(it) }
}