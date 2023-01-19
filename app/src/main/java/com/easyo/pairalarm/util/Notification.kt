package com.easyo.pairalarm.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.work.WorkManager
import com.easyo.pairalarm.R
import com.easyo.pairalarm.broadcast.AlarmReceiver
import com.easyo.pairalarm.ui.activity.MainActivity
import timber.log.Timber

fun makeAlarmNotification(context: Context, messageBody: String) {
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

    // action button 추가
    for (actionButtonOrder in 0..2){
        buildActionButton(context, actionButtonOrder)?.let { notificationBuilder.addAction(it) }
    }

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    val channel = NotificationChannel(
        channelId,
        channelName,
        NotificationManager.IMPORTANCE_DEFAULT
    )
    notificationManager.createNotificationChannel(channel)
    notificationManager.notify(ALARM_NOTI_ID, notificationBuilder.build())

    // Worker를 캔슬하지 않으면 notification을 만들고 Worker로 되돌아감(suspend로 만들어서 그럼)
    WorkManager.getInstance(context).cancelUniqueWork(MAKE_ALARM_WORKER)
}

fun buildActionButton(context: Context, actionButtonOrder: Int): NotificationCompat.Action? {
    val intent = Intent(context, AlarmReceiver::class.java)
    return when (actionButtonOrder) {
        0 -> {
            intent.putExtra(ACTION_BUTTON, NOTI_ACTION1)
            val pendingIntent = PendingIntent.getBroadcast(context, NOTI_ACTION1_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            // TODO: 이 부분은 나중에 유저가 설정할 수 있게 하기
            NotificationCompat.Action.Builder(R.mipmap.ic_launcher, context.getString(R.string.actionButton1), pendingIntent).build()
        }
        1 -> {
            intent.putExtra(ACTION_BUTTON, NOTI_ACTION2)
            val pendingIntent = PendingIntent.getBroadcast(context, NOTI_ACTION2_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            NotificationCompat.Action.Builder(R.mipmap.ic_launcher, context.getString(R.string.actionButton2), pendingIntent).build()
        }
        2 -> {
            intent.putExtra(ACTION_BUTTON, NOTI_ACTION3)
            val pendingIntent = PendingIntent.getBroadcast(context, NOTI_ACTION3_REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            NotificationCompat.Action.Builder(R.mipmap.ic_launcher, context.getString(R.string.actionButton3), pendingIntent).build()
        }
        else -> null
    }
}

fun cancelAlarmNotification(context: Context) {
    Timber.d("cancel notification")
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(ALARM_NOTI_ID)
    WorkManager.getInstance(context).cancelUniqueWork(MAKE_ALARM_WORKER)
}
