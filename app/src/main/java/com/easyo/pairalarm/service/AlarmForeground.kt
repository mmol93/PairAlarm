package com.easyo.pairalarm.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.easyo.pairalarm.R
import com.easyo.pairalarm.ui.activity.MainActivity
import com.easyo.pairalarm.util.ALARM_NOTI_ID
import timber.log.Timber

/**
 * 포그라운드 서비스가 브로드캐스팅이 강제로 취소되는 것을 막아줌
 * */
class AlarmForeground: Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Service activate")

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, getString(R.string.alarm_notification_channel_id))
            .setContentTitle("Foreground Service")
            .setSmallIcon(R.drawable.ic_clock)
            .setContentTitle(getString(R.string.alarm_notification_title))
            .setSound(null)
            .setContentIntent(pendingIntent)
            .build()

        startForeground(ALARM_NOTI_ID, notification)

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Service die")
    }
}