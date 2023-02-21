package com.easyo.pairalarm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.repository.AlarmRepository
import com.easyo.pairalarm.util.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * 새롭게 알람을 추가하고 DB에서 다음 알림을 찾고 바로 등록하거나
 * 그냥 DB에서 다음 알림을 찾고 바로 등록한다
 * */
@HiltWorker
class NextAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmRepository: AlarmRepository,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val actionButtonPosition = inputData.getInt(ACTION_BUTTON, 0)
        if (actionButtonPosition != 0) {
            when (actionButtonPosition) {
                NOTI_ACTION1_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION1_REQUEST_CODE)
                    resetAlarmNotification()
                }
                NOTI_ACTION2_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION2_REQUEST_CODE)
                    resetAlarmNotification()
                }
                NOTI_ACTION3_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION3_REQUEST_CODE)
                    resetAlarmNotification()
                }
            }
        } else {
            resetAlarmNotification()
        }
        return Result.success()
    }

    private suspend fun makeQuickAlarm(notiActionRequestCode: Int) {
        makeAlarmDataForQuickAlarm(notiActionRequestCode)?.let {
            alarmRepository.insertAlarmData(it)
        }
    }

    private fun makeAlarmDataForQuickAlarm(notiActionRequestCode: Int): AlarmData? {
        return when (notiActionRequestCode) {
            NOTI_ACTION1_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(5 * 60 * 1000)
            }
            NOTI_ACTION2_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(15 * 60 * 1000)
            }
            NOTI_ACTION3_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(30 * 60 * 1000)
            }
            else -> null
        }
    }

    private suspend fun resetAlarmNotification() {
        CoroutineScope(Dispatchers.Main).launch {
            alarmRepository.getAllAlarm().collect { alarmDataList ->
                val transformedNextAlarm = getNextAlarm(alarmDataList)
                if (transformedNextAlarm.isNullOrEmpty()) {
                    cancelAlarmNotification(applicationContext)
                } else {
                    makeAlarmNotification(applicationContext, transformedNextAlarm.toString())
                }
                // 모든 알람의 브로드캐스트를 새롭게 지정
                getAllAlarmReset(applicationContext, alarmDataList)
                this.cancel()
            }
        }
    }
}
