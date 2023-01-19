package com.easyo.pairalarm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.repository.AlarmRepository
import com.easyo.pairalarm.util.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber

/**
 * DB에서 다음 알림을 찾고 바로 등록한다
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
                    Timber.d("NOTI_ACTION1_REQUEST_CODE called from worker")
                }
                NOTI_ACTION2_REQUEST_CODE -> {
                    Timber.d("NOTI_ACTION2_REQUEST_CODE called from worker")
                }
                NOTI_ACTION3_REQUEST_CODE -> {
                    Timber.d("NOTI_ACTION3_REQUEST_CODE called from worker")
                }
            }
        } else {
            resetAlarm()
        }
        return Result.success()
    }

    private suspend fun resetAlarm() {
        alarmRepository.getAllAlarm().collectLatest { alarmData ->
            val transformedNextAlarm = getNextAlarm(alarmData)
            if (transformedNextAlarm.isNullOrEmpty()) {
                cancelAlarmNotification(applicationContext)
            } else {
                makeAlarmNotification(applicationContext, transformedNextAlarm.toString())
            }
        }
    }
}
