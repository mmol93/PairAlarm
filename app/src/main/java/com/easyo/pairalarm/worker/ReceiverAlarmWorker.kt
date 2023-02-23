package com.easyo.pairalarm.worker

import android.content.Context
import android.content.Intent
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.ui.activity.OnAlarmActivity
import com.easyo.pairalarm.util.ALARM_CODE_TEXT
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

/**
 * 알람이 울렸을 경우 해당 알람의 정보를 습득하고 지금 울릴 알람이 맞는지 확인한다
 * */
@HiltWorker
class ReceiverAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmDao: AlarmDAO,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val alarmCode = inputData.getString(ALARM_CODE_TEXT)
        Timber.d("alarmCode: $alarmCode")
        if (alarmCode != null) {
            CoroutineScope(Dispatchers.Main).launch {
                val targetAlarmData = alarmDao.searchAlarmDataWithAlarmCode(alarmCode.toString())
                targetAlarmData.collect { alarmData ->
                    Timber.d("Called alarm data: $alarmData")
                    val todayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    Timber.d("today week: $todayWeek")

                    when (todayWeek) {
                        1 -> {
                            if (alarmData.Sun && alarmData.alarmIsOn) {
                                openOnAlarmActivity(applicationContext, alarmCode)
                            }
                        }
                        2 -> {
                            if (alarmData.Mon && alarmData.alarmIsOn) {
                                openOnAlarmActivity(applicationContext, alarmCode)
                            }
                        }
                        3 -> {
                            if (alarmData.Tue && alarmData.alarmIsOn) {
                                openOnAlarmActivity(applicationContext, alarmCode)
                            }
                        }
                        4 -> {
                            if (alarmData.Wed && alarmData.alarmIsOn) {
                                openOnAlarmActivity(applicationContext, alarmCode)
                            }
                        }
                        5 -> {
                            if (alarmData.Thu && alarmData.alarmIsOn) {
                                openOnAlarmActivity(applicationContext, alarmCode)
                            }
                        }
                        6 -> {
                            if (alarmData.Fri && alarmData.alarmIsOn) {
                                openOnAlarmActivity(applicationContext, alarmCode)
                            }
                        }
                        7 -> {
                            if (alarmData.Sat && alarmData.alarmIsOn) {
                                openOnAlarmActivity(applicationContext, alarmCode)
                            }
                        }
                    }
                    // collect를 한 번만 하게 한다
                    this.cancel()
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
