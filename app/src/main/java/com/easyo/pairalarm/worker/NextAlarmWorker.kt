package com.easyo.pairalarm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.util.getNextAlarm
import com.easyo.pairalarm.util.makeAlarmNotification
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@HiltWorker
class NextAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmDao: AlarmDAO
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        alarmDao.getAllAlarms().collectLatest {alarmData->
            val transformedNextAlarm = getNextAlarm(alarmData)
            makeAlarmNotification(applicationContext, transformedNextAlarm.toString())
        }
        return Result.success()
    }
}