package com.easyo.pairalarm.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.R
import com.easyo.pairalarm.eventbus.EventBus
import com.easyo.pairalarm.eventbus.InitDataEvent
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class InitAlarmDataWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        EventBus.post(InitDataEvent(Int.MAX_VALUE, applicationContext.getString(R.string.progressInit)))
        return Result.success()
    }
}
