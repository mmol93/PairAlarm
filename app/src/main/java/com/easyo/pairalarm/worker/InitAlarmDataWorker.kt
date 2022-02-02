package com.easyo.pairalarm.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.eventbus.EventBus
import com.easyo.pairalarm.eventbus.InitDataEvent
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
        EventBus.post(InitDataEvent(Int.MAX_VALUE, applicationContext.getString(R.string.progressInit)))
        Log.d("InitWorker", "run")
        return Result.success()
    }
}