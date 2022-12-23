package com.easyo.pairalarm.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import androidx.room.Room
import com.easyo.pairalarm.database.AppDatabase
import com.easyo.pairalarm.database.table.AlarmData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

fun setAlarm(context: Context, alarmCode: Int, hour: Int, min: Int) {
    val setAlarmCalendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, 0)
    }

    var calendarMillis = setAlarmCalendar.timeInMillis
    val currentTimeMillis = Calendar.getInstance().timeInMillis

    // 지정한 시간이 현재 시간보다 과거일 경우 + interval을 해줘야한다
    if (currentTimeMillis > calendarMillis) {
        val intervalOneDay = (24 * 60 * 60 * 1000).toLong() // 24시간
        calendarMillis += intervalOneDay
    }

    val intent = Intent(context.packageName)
    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
    intent.component = ComponentName(context.packageName, "com.easyo.pairalarm.broadcast.MyReceiver")
    intent.putExtra(ALARM_CODE_TEXT, "$alarmCode")

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarmCode,
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val alarmInfo = AlarmManager.AlarmClockInfo(calendarMillis, pendingIntent)
    Timber.d("set alarmCode: $alarmCode")
    Timber.d("set alarm(mills): " + transMillisToTime(calendarMillis))
    alarmManager.setAlarmClock(alarmInfo, pendingIntent)
}

// 모든 알람 데이터를 가져와서 전부 다시 셋팅한다
fun resetAlarm(context: Context?) {
    if (context != null) {
        val alarmData: AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, ALARM_DB_NAME).build()
        CoroutineScope(Dispatchers.IO).launch {
            alarmData.alarmDao().getAllAlarms().collectLatest {
                it.forEach { alarmData ->
                    Timber.d("reset alarm: " + alarmData.alarmCode)
                    setAlarm(
                        context,
                        alarmData.alarmCode.toInt(),
                        alarmData.hour,
                        alarmData.minute
                    )
                }
                getNextAlarm(it)?.let { message -> makeAlarmNotification(context, message) }
            }
        }
    }
}

fun cancelAlarm(context: Context, alarmCode: String) {
    val intent = Intent(context.packageName)
    intent.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
    intent.component = ComponentName(context.packageName, "com.easyo.pairalarm.MyReceiver")
    val pendingIntent = PendingIntent.getBroadcast(
        context,
        alarmCode.toInt(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
    val alarmManager: AlarmManager? =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager?

    alarmManager?.cancel(pendingIntent)
    pendingIntent.cancel()
}

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
