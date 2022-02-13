package com.easyo.pairalarm.util

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData

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