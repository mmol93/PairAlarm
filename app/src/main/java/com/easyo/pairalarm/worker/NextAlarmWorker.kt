package com.easyo.pairalarm.worker

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.eventbus.EventBus
import com.easyo.pairalarm.eventbus.InitDataEvent
import com.easyo.pairalarm.util.getMillisWithCalendar
import com.easyo.pairalarm.util.makeAlarmNotification
import com.easyo.pairalarm.util.transMillisToTime
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
        AppClass.alarmDataList = alarmDao.getAllAlarms()
        CoroutineScope(Dispatchers.IO).launch {
            AppClass.alarmDataList?.collectLatest {
                val appClass = AppClass()
                appClass.getAlarmTimeList().clear()
                it.forEach { alarmData ->
                    if (alarmData.Sun) {
                        appClass.getAlarmTimeList()
                            .add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 1))
                    }
                    if (alarmData.Mon) {
                        appClass.getAlarmTimeList()
                            .add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 2))
                    }
                    if (alarmData.Tue) {
                        appClass.getAlarmTimeList()
                            .add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 3))
                    }
                    if (alarmData.Wed) {
                        appClass.getAlarmTimeList()
                            .add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 4))
                    }
                    if (alarmData.Thu) {
                        appClass.getAlarmTimeList()
                            .add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 5))
                    }
                    if (alarmData.Fri) {
                        appClass.getAlarmTimeList()
                            .add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 6))
                    }
                    if (alarmData.Sat) {
                        appClass.getAlarmTimeList()
                            .add(getMillisWithCalendar(alarmData.hour, alarmData.minute, 7))
                    }
                }
                appClass.getAlarmTimeList().minOrNull()
                    ?.let { closestAlarm -> appClass.setClosestAlarm(closestAlarm)
                        Log.d("AlarmWorker", "closest alarm: $closestAlarm")}

                val transformedNextAlarm = transMillisToTime(appClass.getClosestAlarm())

                makeAlarmNotification(applicationContext, transformedNextAlarm.toString())
            }
        }
        return Result.success()
    }
}