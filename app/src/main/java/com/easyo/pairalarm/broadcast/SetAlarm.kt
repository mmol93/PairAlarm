package com.easyo.pairalarm.broadcast

import android.content.Context
import android.content.Intent
import java.util.*

fun setNormalAlarm(context: Context, requestCode: Long, hour: Int, min: Int){
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

    val intent = Intent(context, MyReceiver::class.java)

}