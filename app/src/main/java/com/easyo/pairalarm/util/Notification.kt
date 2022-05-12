package com.easyo.pairalarm.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import com.easyo.pairalarm.MainActivity
import com.easyo.pairalarm.R

fun makeAlarmNotification(context: Context, messageBody: String) {
    Log.d("notification", "make notification")
    // noti 클릭 시 MainActivity를 열게 한다
    val intent = Intent(context, MainActivity::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
    val pendingIntent = PendingIntent.getActivity(
        context, 0, intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val channelId = context.getString(R.string.alarm_notification_channel_id)
    val channelName = context.getString(R.string.alarm_notification_channel_name)

    val notificationBuilder = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_clock)
        .setContentTitle(context.getString(R.string.alarm_notification_title))
        .setContentText(messageBody)
        .setAutoCancel(false)   // 전체 삭제해도 안되게하기
        .setSound(null)
        .setContentIntent(pendingIntent)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setOngoing(true)   // 알람이 계속 뜬 상태로 있게하기

    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationManager.createNotificationChannel(channel)
    notificationManager.notify(ALARM_NOTI_ID, notificationBuilder.build())

    // Worker를 캔슬하지 않으면 notification을 만들고 Worker로 되돌아감(suspend로 만들어서 그럼)
    WorkManager.getInstance(context).cancelUniqueWork("makeNotification")
}

fun cancelAlarmNotification(context: Context) {
    Log.d("notification", "cancel notification")
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(ALARM_NOTI_ID)
}