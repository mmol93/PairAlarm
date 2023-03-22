package com.easyo.pairalarm

import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.util.getNextAlarm
import org.junit.Test

class NextAlarmTest {

    private val testAlarmDataList: MutableList<AlarmData> =
        // everyDay 9:20
        mutableListOf(
            AlarmData(
                null,
                alarmIsOn = true,
                Sun = true,
                Mon = true,
                Tue = true,
                Wed = true,
                Thu = true,
                Fri = true,
                Sat = true,
                hour = 9,
                minute = 20,
                volume = 100,
                vibration = 0,
                quick = false,
                bell = 100,
                mode = 0,
                name = "",
                alarmCode = "1"
            ),
            // everyDay 21:30
            AlarmData(
                null,
                alarmIsOn = true,
                Sun = true,
                Mon = true,
                Tue = true,
                Wed = true,
                Thu = true,
                Fri = true,
                Sat = true,
                hour = 21,
                minute = 30,
                volume = 100,
                vibration = 0,
                quick = false,
                bell = 100,
                mode = 0,
                name = "",
                alarmCode = "2"
            ),
            // everyData 13:20
            AlarmData(
                null,
                alarmIsOn = true,
                Sun = true,
                Mon = true,
                Tue = true,
                Wed = true,
                Thu = true,
                Fri = true,
                Sat = true,
                hour = 13,
                minute = 20,
                volume = 100,
                vibration = 0,
                quick = false,
                bell = 100,
                mode = 0,
                name = "",
                alarmCode = "3"
            ),
            // everyDay 17:20
            AlarmData(
                null,
                alarmIsOn = true,
                Sun = true,
                Mon = true,
                Tue = true,
                Wed = true,
                Thu = true,
                Fri = true,
                Sat = true,
                hour = 17,
                minute = 20,
                volume = 100,
                vibration = 0,
                quick = false,
                bell = 100,
                mode = 0,
                name = "",
                alarmCode = "3"
            )
        )

    @Test
    fun `find next alarm`() {
        // result will be 9:20 or 13:20 or 17:20 or 21:30
        val nextAlarm = getNextAlarm(testAlarmDataList)
        if (nextAlarm?.contains("9:20") == true
            || nextAlarm?.contains("13:20") == true
            || nextAlarm?.contains("17:20") == true
            || nextAlarm?.contains("21:30") == true
        ) {
            println("next alarm : $nextAlarm")
        } else {
            throw java.lang.RuntimeException("wrong next time")
        }
    }
}