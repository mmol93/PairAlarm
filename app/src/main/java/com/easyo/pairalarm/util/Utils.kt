package com.easyo.pairalarm.util

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import java.util.*

fun getNewAlarmCode(): String {
    val calendar = Calendar.getInstance()
    val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
    val currentMin = calendar.get(Calendar.MINUTE)
    val currentSecond = calendar.get(Calendar.SECOND)

    return currentDay.toString() + currentHour.toString() +
            currentMin.toString() + currentSecond.toString()
}

fun makeToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun selectMusic(context: Context, index: Int): MediaPlayer {
    return when (index) {
        0 -> MediaPlayer.create(context, R.raw.normal_walking)
        1 -> MediaPlayer.create(context, R.raw.normal_pianoman)
        2 -> MediaPlayer.create(context, R.raw.normal_happytown)
        3 -> MediaPlayer.create(context, R.raw.normal_loney)
        else -> MediaPlayer.create(context, R.raw.normal_walking)
    }
}

// SimpleAlarm 이나 QuickAlarm 으로 만든 알람의 calendar 값을 얻는다
fun getAddedTime(hour: Int, min: Int): Calendar {
    val calendar = Calendar.getInstance().apply {
        add(Calendar.HOUR, hour)
        add(Calendar.MINUTE, min)
        timeInMillis
    }
    return calendar
}

// AlarmData를 만드는데 필요한 데이터를 매개변수로 받고 AlarmData를 반환 - SimpleAlarm과 QuickAlarm에서 사용된다
fun makeAlarmData(
    calendar: Calendar,
    alarmName: String,
    alarmData: AlarmData
): AlarmData {
    val setWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val vibration = alarmData.vibration
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val min = calendar.get(Calendar.MINUTE)
    val volume = alarmData.volume
    val bell = alarmData.bell
    val mode = alarmData.mode
    val alarmCode = getNewAlarmCode()

    // 모든 요일이 false인 상태
    val defaultAlarmData = AlarmData(
        id = null,
        button = true,
        Sun = false,
        Mon = false,
        Tue = false,
        Wed = false,
        Thu = false,
        Fri = false,
        Sat = false,
        vibration = vibration,
        alarmCode = alarmCode,
        mode = mode,
        hour = hour,
        minute = min,
        quick = true,
        volume = volume,
        bell = bell,
        name = alarmName
    )

    // 일요일=1 ~ 토요일 = 7임
    return when (setWeek) {
        // 일
        1 -> defaultAlarmData.copy(Sun = true)
        2 -> defaultAlarmData.copy(Mon = true)
        3 -> defaultAlarmData.copy(Tue = true)
        4 -> defaultAlarmData.copy(Wed = true)
        5 -> defaultAlarmData.copy(Thu = true)
        6 -> defaultAlarmData.copy(Fri = true)
        else -> defaultAlarmData.copy(Sat = true)
    }
}

