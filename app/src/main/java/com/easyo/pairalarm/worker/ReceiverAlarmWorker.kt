package com.easyo.pairalarm.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.ui.activity.OnAlarmActivity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.collectLatest
import java.util.*

@HiltWorker
class ReceiverAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmDao: AlarmDAO
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val requestCode = inputData.getString("requestCode")
        Log.d("ReceiverAlarmWorker", "requestCode: $requestCode")

        if (requestCode != null) {
            val targetAlarmData = alarmDao.searchAlarmDataWithAlarmCode(requestCode.toString())
            targetAlarmData.collectLatest { alarmDataList ->
                if (alarmDataList.isNotEmpty()) {
                    Log.d("ReceiverAlarmWorker", "Called alarm data: $alarmDataList")
                    val todayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    Log.d("ReceiverAlarmWorker", "today week: $todayWeek")

                    when (todayWeek) {
                        1 -> {
                            if (alarmDataList[0].Sun && alarmDataList[0].button) {
                                openOnAlarmActivity(applicationContext, requestCode)
                            }
                        }
                        2 -> {
                            if (alarmDataList[0].Mon && alarmDataList[0].button) {
                                openOnAlarmActivity(applicationContext, requestCode)
                            }
                        }
                        3 -> {
                            if (alarmDataList[0].Tue && alarmDataList[0].button) {
                                openOnAlarmActivity(applicationContext, requestCode)
                            }
                        }
                        4 -> {
                            if (alarmDataList[0].Wed && alarmDataList[0].button) {
                                openOnAlarmActivity(applicationContext, requestCode)
                            }
                        }
                        5 -> {
                            if (alarmDataList[0].Thu && alarmDataList[0].button) {
                                openOnAlarmActivity(applicationContext, requestCode)
                            }
                        }
                        6 -> {
                            if (alarmDataList[0].Fri && alarmDataList[0].button) {
                                openOnAlarmActivity(applicationContext, requestCode)
                            }
                        }
                        7 -> {
                            if (alarmDataList[0].Sat && alarmDataList[0].button) {
                                openOnAlarmActivity(applicationContext, requestCode)
                            }
                        }
                    }
                }
            }
        }
        return Result.success()
    }
}

fun openOnAlarmActivity(context: Context, requestCode: String) {
    val onAlarmActivity = Intent(context, OnAlarmActivity::class.java)
    onAlarmActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    onAlarmActivity.putExtra("requestCode", requestCode)
    context.startActivity(onAlarmActivity)
}
