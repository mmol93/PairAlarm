package com.easyo.pairalarm.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.easyo.pairalarm.util.ALARM_NOTI_ID
import com.easyo.pairalarm.util.NEXT_ALARM_NOTIFICATION_TEXT
import com.easyo.pairalarm.util.getNotificationBuilder
import com.easyo.pairalarm.worker.NextAlarmWorker
import timber.log.Timber
import java.util.concurrent.TimeUnit

/**
 * 포그라운드 서비스가 브로드캐스팅이 강제로 취소되는 것을 막아줌
 * */
class AlarmForeground : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Timber.d("Service activated")

        startForeground(
            ALARM_NOTI_ID,
            getNotificationBuilder(
                this,
                intent?.getStringExtra(NEXT_ALARM_NOTIFICATION_TEXT) ?: ""
            ).build()
        )

        val alarmTimeWorkRequest: WorkRequest =
            PeriodicWorkRequest.Builder(NextAlarmWorker::class.java, 4, TimeUnit.HOURS).build()

        WorkManager.getInstance(applicationContext).enqueue(alarmTimeWorkRequest)

        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.d("Service die")
    }
}
