package com.EasyO.pairalarm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.EasyO.pairalarm.AppClass
import com.EasyO.pairalarm.database.dao.AlarmDAO
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InitAlarmDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmDao: AlarmDAO
): CoroutineWorker(appContext, workerParams){
    override suspend fun doWork(): Result {
        AppClass.alarmData =  alarmDao.getAllAlarms()
        return Result.success()
    }
}