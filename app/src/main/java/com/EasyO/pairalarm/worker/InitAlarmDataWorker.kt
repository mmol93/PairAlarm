package com.EasyO.pairalarm.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.EasyO.pairalarm.AppClass
import com.EasyO.pairalarm.R
import com.EasyO.pairalarm.database.dao.AlarmDAO
import com.EasyO.pairalarm.eventbus.EventBus
import com.EasyO.pairalarm.eventbus.InitDataEvent
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