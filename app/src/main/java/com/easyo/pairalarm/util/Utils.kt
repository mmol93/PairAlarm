package com.easyo.pairalarm.util

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.viewModel.SimpleAlarmViewModel
import java.util.*

fun makeToast(context: Context, message: String){
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

fun selectMusic(context: Context, index: Int): MediaPlayer{
    return when(index){
        0 -> MediaPlayer.create(context, R.raw.normal_walking)
        1 -> MediaPlayer.create(context, R.raw.normal_pianoman)
        2 -> MediaPlayer.create(context, R.raw.normal_happytown)
        3 -> MediaPlayer.create(context, R.raw.normal_loney)
        else -> MediaPlayer.create(context, R.raw.normal_walking)
    }
}

// SimpleAlarm 이나 QuickAlarm 으로 만든 알람의 calendar 값을 얻는다
fun getAddedTime(hour: Int, min: Int): Calendar{
    val calendar = Calendar.getInstance().apply {
        add(Calendar.HOUR, hour)
        add(Calendar.MINUTE, min)
        timeInMillis
    }
//    Log.d("SimpleAlarmActivity", "calendar.timeInMillis: ${calendar.timeInMillis}")
    return calendar
}

// AlarmData를 만드는데 필요한 데이터를 매개변수로 받고 AlarmData를 반환 - SimpleAlarm과 QuickAlarm에서 사용된다
fun makeAlarmData(calendar: Calendar, alarmName: String, simpleAlarmViewModel: SimpleAlarmViewModel): AlarmData{
    val setWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val vibration = simpleAlarmViewModel.currentAlarmVibration.value
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val min = calendar.get(Calendar.MINUTE)
    val volume = simpleAlarmViewModel.currentAlarmVolume.value
    val bell = simpleAlarmViewModel.currentAlarmBell.value
    val mode = simpleAlarmViewModel.currentAlarmMode.value
    val currentCalendar = Calendar.getInstance()
    val currentDay = currentCalendar.get(Calendar.DAY_OF_YEAR)
    val currentHour = currentCalendar.get(Calendar.HOUR_OF_DAY)
    val currentMin = currentCalendar.get(Calendar.MINUTE)
    val currentSecond = currentCalendar.get(Calendar.SECOND)
    val alarmCode = currentDay.toString() + currentHour.toString() +
            currentMin.toString() + currentSecond.toString()

    // 일요일=1 ~ 토요일 = 7임
    return when(setWeek){
        // 일
        1 -> AlarmData(id = null, button = true, Sun = true, Mon = false, Tue = false, Wed = false, Thu = false, Fri = false, Sat = false, vibration = vibration, alarmCode = alarmCode, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        2 -> AlarmData(id = null, button = true, Sun = false, Mon = true, Tue = false, Wed = false, Thu = false, Fri = false, Sat = false, vibration = vibration, alarmCode = alarmCode, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        3 -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = true, Wed = false, Thu = false, Fri = false, Sat = false, vibration = vibration, alarmCode = alarmCode, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        4 -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = false, Wed = true, Thu = false, Fri = false, Sat = false, vibration = vibration, alarmCode = alarmCode, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        5 -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = false, Wed = false, Thu = true, Fri = false, Sat = false, vibration = vibration, alarmCode = alarmCode, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        6 -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = false, Wed = false, Thu = false, Fri = true, Sat = false, vibration = vibration, alarmCode = alarmCode, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        else -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = false, Wed = false, Thu = false, Fri = false, Sat = true, vibration = vibration, alarmCode = alarmCode, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
    }
}

