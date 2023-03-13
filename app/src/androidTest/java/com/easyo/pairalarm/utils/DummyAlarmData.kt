package com.easyo.pairalarm.utils

import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.util.getCurrentHour
import com.easyo.pairalarm.util.getCurrentMinute

fun initCurrentAlarmDataForTest(
    hour: Int = 0,
    min: Int = 0,
    isQuick: Boolean = false,
    alarmCode: String = ""
): AlarmData {
    return AlarmData(
        id = 1,
        alarmIsOn = true,
        Sun = false,
        Mon = false,
        Tue = false,
        Wed = false,
        Thu = false,
        Fri = false,
        Sat = false,
        vibration = 0,
        alarmCode = alarmCode,
        mode = 0,
        hour = if (hour != 0) hour else getCurrentHour(),
        minute = if (min != 0) min else getCurrentMinute(),
        quick = isQuick,
        volume = 100,
        bell = 0,
        name = ""
    )
}