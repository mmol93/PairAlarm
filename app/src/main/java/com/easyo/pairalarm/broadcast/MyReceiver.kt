package com.easyo.pairalarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.easyo.pairalarm.util.resetAlarm
import com.easyo.pairalarm.worker.ReceiverAlarmWorker

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
        // 그 외의 모든 알람(= 내가 설정한 알람)
        else {
            Log.d("MyReceiver", "alarm!")
            val alarmCode = intent.getStringExtra("alarmCode")
            Log.d("MyReceiver", "alarmCode: $alarmCode")

            if (alarmCode != null) {
                val workData = workDataOf("alarmCode" to alarmCode)
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
