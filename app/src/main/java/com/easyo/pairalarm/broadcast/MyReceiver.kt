package com.easyo.pairalarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.*
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.alarm.resetAlarm
import com.easyo.pairalarm.ui.activity.OnAlarmActivity
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
            val requestCode = intent.getStringExtra("requestCode")
            Log.d("MyReceiver", "requestCode: $requestCode")

            if (requestCode != null) {
                AppClass.requestCode = requestCode
                val receiverAlarmWorkRequest: WorkRequest =
                    OneTimeWorkRequestBuilder<ReceiverAlarmWorker>().build()
                WorkManager.getInstance(context!!).enqueueUniqueWork(
                    "onAlarmActivity",
                    ExistingWorkPolicy.KEEP,
                    receiverAlarmWorkRequest as OneTimeWorkRequest
                )
            }
        }
    }
}