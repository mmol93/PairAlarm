package com.easyo.pairalarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.easyo.pairalarm.util.ALARM_CODE_TEXT
import com.easyo.pairalarm.util.resetAlarm
import com.easyo.pairalarm.worker.ReceiverAlarmWorker
import timber.log.Timber

class MyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        // ** 휴대폰을 재부팅 했을 때 -> 모든 알람을 재설정
        if (intent!!.action == "android.intent.action.BOOT_COMPLETED" ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            resetAlarm(context)
        }
        // 앱을 업데이트 했을 때 -> 모든 알람을 재설정
        else if (intent.action == "android.intent.action.MY_PACKAGE_REPLACED") {
            resetAlarm(context)
        }
        // 내가 설정한 알람이 울렸을 때
        else {
            val alarmCode = intent.getStringExtra(ALARM_CODE_TEXT)
            Timber.d("alarmCode: $alarmCode")

            if (alarmCode != null) {
                val workData = workDataOf(ALARM_CODE_TEXT to alarmCode)
                val receiverAlarmWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<ReceiverAlarmWorker>()
                        .setInputData(workData)
                        .build()
                WorkManager.getInstance(context!!).enqueueUniqueWork(
                    "onAlarmActivity",
                    ExistingWorkPolicy.KEEP,
                    receiverAlarmWorkRequest as OneTimeWorkRequest
                )
            }
        }
    }
}
