package com.easyo.pairalarm.broadcast

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import com.easyo.pairalarm.util.transMillisToTime
import java.util.*

fun setNormalAlarm(context: Context, requestCode: Int, hour: Int, min: Int){
    val setAlarmCalendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, 0)
    }

    var calendarMillis = setAlarmCalendar.timeInMillis
    val currentTimeMillis = Calendar.getInstance().timeInMillis

    // 지정한 시간이 현재 시간보다 과거일 경우 + interval을 해줘야한다
    if (currentTimeMillis > calendarMillis){
        val intervalOneDay = (24 * 60 * 60 * 1000).toLong() // 24시간
        calendarMillis += intervalOneDay
    }

    val intent = Intent("com.easyo.pairalarm")
    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
    intent.component = ComponentName("com.easyo.pairalarm", "com.easyo.pairalarm.broadcast.MyReceiver")
    intent.putExtra("alarm$requestCode", "alarm$requestCode")

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmInfo = AlarmManager.AlarmClockInfo(calendarMillis, pendingIntent)
    Log.d("Alarm", "set alarm(mills): ${transMillisToTime(calendarMillis)}")
    alarmManager.setAlarmClock(alarmInfo, pendingIntent)
    getNextAlarm(context)
}

fun getNextAlarm(context: Context){
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    Log.d("Alarm", "next alarm is ${transMillisToTime(alarmManager.nextAlarmClock.triggerTime)}")
}

fun cancelAlarm(context: Context, requestCode: String){
    val intent = Intent("com.easyo.pairalarm")
    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
    intent.component = ComponentName("com.easyo.pairalarm", "com.easyo.pairalarm.MyReceiver")
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        requestCode.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    alarmManager?.cancel(pendingIntent)
    pendingIntent.cancel()
}
