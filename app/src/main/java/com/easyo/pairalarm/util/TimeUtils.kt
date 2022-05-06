package com.easyo.pairalarm.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun transMillisToTime(milliSecond: Long): String? {
    val simpleDateFormat = SimpleDateFormat("MM/dd hh:mm")
    return simpleDateFormat.format(milliSecond)
}

fun getMillisWithCalendar(hour: Int, min: Int, dayOfWeek: Int): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, 0)
        set(Calendar.DAY_OF_WEEK, dayOfWeek)
    }
    val alarmMillis = calendar.timeInMillis

    if (alarmMillis <= Calendar.getInstance().timeInMillis){
        calendar.add(Calendar.DAY_OF_WEEK, 7)
    }

    return calendar.timeInMillis
}