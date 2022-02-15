package com.easyo.pairalarm.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.widget.Toast
import com.easyo.pairalarm.AppClass
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

fun getAddedTime(hour: Int, min: Int): Calendar{
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.HOUR, hour)
    calendar.add(Calendar.MINUTE, min)
    calendar.timeInMillis
//    Log.d("SimpleAlarmActivity", "calendar.timeInMillis: ${calendar.timeInMillis}")
    return calendar
}

fun getWeekDataFromCalendar(calendar: Calendar): Int{
    return calendar.get(Calendar.DAY_OF_WEEK)
}


fun makeAlarmData(calendar: Calendar, alarmName: String, simpleAlarmViewModel: SimpleAlarmViewModel): AlarmData{
    val DAY_OF_WEEK = calendar.get(Calendar.DAY_OF_WEEK)
    val currentTimeMillis = Calendar.getInstance().timeInMillis
    val vibration = simpleAlarmViewModel.currentAlarmVibration.value
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val min = calendar.get(Calendar.MINUTE)
    val volume = simpleAlarmViewModel.currentAlarmVolume.value
    val bell = simpleAlarmViewModel.currentAlarmBell.value
    val mode = simpleAlarmViewModel.currentAlarmMode.value

    // 일요일=1 ~ 토요일 = 7임
    return when(DAY_OF_WEEK){
        // 일
        1 -> AlarmData(id = null, button = true, Sun = true, Mon = false, Tue = false, Wed = false, Thu = false, Fri = false, Sat = false, vibration = vibration, requestCode = currentTimeMillis, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        2 -> AlarmData(id = null, button = true, Sun = false, Mon = true, Tue = false, Wed = false, Thu = false, Fri = false, Sat = false, vibration = vibration, requestCode = currentTimeMillis, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        3 -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = true, Wed = false, Thu = false, Fri = false, Sat = false, vibration = vibration, requestCode = currentTimeMillis, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        4 -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = false, Wed = true, Thu = false, Fri = false, Sat = false, vibration = vibration, requestCode = currentTimeMillis, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        5 -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = false, Wed = false, Thu = true, Fri = false, Sat = false, vibration = vibration, requestCode = currentTimeMillis, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        6 -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = false, Wed = false, Thu = false, Fri = true, Sat = false, vibration = vibration, requestCode = currentTimeMillis, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
        else -> AlarmData(id = null, button = true, Sun = false, Mon = false, Tue = false, Wed = false, Thu = false, Fri = false, Sat = true, vibration = vibration, requestCode = currentTimeMillis, mode = mode, hour = hour, minute = min, quick = true, volume = volume, bell = bell, name = alarmName)
    }
}

fun initCurrentAlarmData() {
    AppClass.alarmViewModel.currentAlarmData.value = AlarmData(
        id = null,
        button = true,
        Sun = false,
        Mon = false,
        Tue = false,
        Wed = false,
        Thu = false,
        Fri = false,
        Sat = false,
        vibration = 0,
        requestCode = 0,
        mode = 0,
        hour = 1,
        minute = 0,
        quick = false,
        volume = 100,
        bell = 0,
        name = ""
    )
}