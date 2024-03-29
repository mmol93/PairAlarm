package com.easyo.pairalarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.*
import com.easyo.pairalarm.BuildConfig
import com.easyo.pairalarm.service.AlarmForeground
import com.easyo.pairalarm.util.*
import com.easyo.pairalarm.worker.NextAlarmWorker
import com.easyo.pairalarm.worker.ReceiverAlarmWorker
import timber.log.Timber

class AlarmReceiver : BroadcastReceiver() {
    var broadcastCalled = false
    override fun onReceive(context: Context?, intent: Intent?) {
        println("Broadcast is called")
        broadcastCalled = true
        val alarmCode = intent?.getStringExtra(ALARM_CODE_TEXT)
        val actionButtonCode = intent?.getStringExtra(ACTION_BUTTON)
        if (BuildConfig.DEBUG) {
            context?.let {
                val preference = SharedPreference(it)
                when {
                    alarmCode != null -> preference.putStringData(
                        preference.getStringData(),
                        alarmCode
                    )
                    intent?.action != "" -> preference.putStringData(
                        preference.getStringData(),
                        intent?.action
                    )
                    else -> preference.putStringData(
                        preference.getStringData(),
                        "no intent & alarmCode info"
                    )
                }
            }
        }

        if (intent != null && context != null) {
            when {
                // ** 휴대폰을 재부팅 했을 때 & 앱을 업데이트 했을 때-> 모든 알람을 재설정
                intent.action == "android.intent.action.BOOT_COMPLETED" ||
                        intent.action == "android.intent.action.QUICKBOOT_POWERON" ||
                        intent.action == "android.intent.action.MY_PACKAGE_REPLACED" ||
                        intent.action == "android.intent.action.REBOOT" ||
                        intent.action == "android.intent.action.LOCKED_BOOT_COMPLETED"
                -> {
                    Timber.d("reset alarm")
                    // 서비스 재시작
                    val serviceIntent = Intent(context, AlarmForeground::class.java)
                    context.startForegroundService(serviceIntent)
                }

                // 내가 설정한 알람이 울렸을 때
                alarmCode != null -> {
                    val workData = workDataOf(ALARM_CODE_TEXT to alarmCode)
                    val receiverAlarmWorkRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<ReceiverAlarmWorker>()
                            .setInputData(workData)
                            .build()
                    WorkManager.getInstance(context).enqueueUniqueWork(
                        RECEIVER_ALARM_WORKER,
                        ExistingWorkPolicy.REPLACE,
                        receiverAlarmWorkRequest as OneTimeWorkRequest
                    )
                }

                // Notification에 있는 actionButton을 눌렀을 때
                actionButtonCode != null -> {
                    when (actionButtonCode) {
                        NOTI_ACTION1 -> {
                            doNextWorkAlarm(context, NOTI_ACTION1_REQUEST_CODE)
                        }
                        NOTI_ACTION2 -> {
                            doNextWorkAlarm(context, NOTI_ACTION2_REQUEST_CODE)
                        }
                        NOTI_ACTION3 -> {
                            doNextWorkAlarm(context, NOTI_ACTION3_REQUEST_CODE)
                        }
                    }
                }
            }
        }
    }

    private fun doNextWorkAlarm(context: Context?, dataValue: Int) {
        context?.let {
            val alarmTimeWorkRequest: WorkRequest =
                OneTimeWorkRequestBuilder<NextAlarmWorker>()
                    .setInputData(Data.Builder().putInt(ACTION_BUTTON, dataValue).build())
                    .build()
            WorkManager.getInstance(it)
                .enqueueUniqueWork(
                    NEXT_ALARM_WORKER,
                    ExistingWorkPolicy.REPLACE,
                    alarmTimeWorkRequest as OneTimeWorkRequest
                )
        }
    }
}
