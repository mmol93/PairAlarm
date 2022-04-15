package com.easyo.pairalarm.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.easyo.pairalarm.MainActivity
import com.easyo.pairalarm.R
import com.easyo.pairalarm.util.ACTION_STOP
import com.easyo.pairalarm.util.ALARM_NOTI_ID

// Foreground로 항상 앱을 켜놓을 용도로 사용
class MyService: Service() {
    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action != null && intent.action.equals(ACTION_STOP, ignoreCase = true)) {
            stopSelf()
        }

        // todo DB에 있는 모든 알람을 alarmManager에 등록하기


        makeAlarmNotification(this, "normal alarm")

        return START_STICKY
    }

    // Notification 생성 - 알람 전용(슬라이드로 제거 불가)
    // foreground service에서 사용
    private fun makeAlarmNotification(context: Context, messageBody: String){
        // noti 클릭 시 MainActivity를 열게 한다
        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

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
        startForeground(ALARM_NOTI_ID, notificationBuilder.build())
    }
}
