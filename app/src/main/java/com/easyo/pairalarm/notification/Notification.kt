package com.easyo.pairalarm.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.easyo.pairalarm.Constant.ALARM_NOTI_ID
import com.easyo.pairalarm.MainActivity
import com.easyo.pairalarm.R

// Notification 생성 - 알람 전용(슬라이드로 제거 불가)
fun makeNotificationForAlarm(context: Context, title: String, messageBody: String){
    // noti 클릭 시 MainActivity를 열게 한다
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    val pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent,
        PendingIntent.FLAG_UPDATE_CURRENT)

    val channelId = context.getString(R.string.alarm_notification_channel_id)
    val channelName = context.getString(R.string.alarm_notification_channel_name)

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_clock)
        .setContentTitle(context.getString(R.string.app_name))
        .setContentText(messageBody)
        .setAutoCancel(false)   // 전체 삭제해도 안되게하기
        .setSound(null)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setOngoing(true)   // 알람이 계속 뜬 상태로 있게하기

    // noti에서 사용할 채널을 만든다(API26 이상에서는 반드시 채널 필요)
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(channelId,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT)
    notificationManager.createNotificationChannel(channel)

    // noti 생성
    notificationManager.notify(ALARM_NOTI_ID, notificationBuilder.build())
}

// Alarm notification 삭제
fun cancelNotificationForAlarm(context: Context){
    val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.cancel(ALARM_NOTI_ID)
}