package com.easyo.pairalarm.util

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
fun transMillisToTime(milliSecond: Long): String? {
    val simpleDateFormat = SimpleDateFormat("MM/dd hh:mm")
    return simpleDateFormat.format(milliSecond)
}

fun getCurrentHour(): Int {
    return Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
}

fun getCurrentMinute(): Int {
    return Calendar.getInstance().get(Calendar.MINUTE)
}

fun getCurrentMinuteDoubleDigitWithString(): String {
    val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)
    return if (currentMinute < 10) {
        "0$currentMinute"
    } else {
        currentMinute.toString()
    }
}

fun getCurrentHourDoubleDigitWithString(): String {
    val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return if (currentHour < 10) {
        "0$currentHour"
    } else {
        currentHour.toString()
    }
}

fun getMillisWithCalendar(hour: Int, min: Int, dayOfWeek: Int): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, min)
        set(Calendar.SECOND, 0)
        set(Calendar.DAY_OF_WEEK, dayOfWeek)
    }
    val alarmMillis = calendar.timeInMillis

    if (alarmMillis <= Calendar.getInstance().timeInMillis) {
        calendar.add(Calendar.DAY_OF_WEEK, 7)
    }

    return calendar.timeInMillis
}