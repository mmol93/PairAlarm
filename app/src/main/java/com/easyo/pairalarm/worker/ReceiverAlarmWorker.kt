package com.easyo.pairalarm.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.ui.activity.OnAlarmActivity
import com.easyo.pairalarm.util.ALARM_CODE_TEXT
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import timber.log.Timber
import java.util.*

@HiltWorker
class ReceiverAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmDao: AlarmDAO
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val alarmCode = inputData.getString(ALARM_CODE_TEXT)
        Timber.d("alarmCode: $alarmCode")

        if (alarmCode != null) {
            val targetAlarmData = alarmDao.searchAlarmDataWithAlarmCode(alarmCode.toString())
            targetAlarmData.collectLatest { alarmData ->
                Timber.d("Called alarm data: $alarmData")
                val todayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                Timber.d("today week: $todayWeek")

                when (todayWeek) {
                    1 -> {
                        if (alarmData.Sun && alarmData.button) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    2 -> {
                        if (alarmData.Mon && alarmData.button) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    3 -> {
                        if (alarmData.Tue && alarmData.button) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    4 -> {
                        if (alarmData.Wed && alarmData.button) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    5 -> {
                        if (alarmData.Thu && alarmData.button) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    6 -> {
                        if (alarmData.Fri && alarmData.button) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                    7 -> {
                        if (alarmData.Sat && alarmData.button) {
                            openOnAlarmActivity(applicationContext, alarmCode)
                        }
                    }
                }
            }
        }
        return Result.success()
    }
}

fun openOnAlarmActivity(context: Context, alarmCode: String) {
    val onAlarmActivity = Intent(context, OnAlarmActivity::class.java)
    onAlarmActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    onAlarmActivity.putExtra(ALARM_CODE_TEXT, alarmCode)
    context.startActivity(onAlarmActivity)
}
