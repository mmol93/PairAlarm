package com.easyo.pairalarm.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.AppClass.Companion.requestCode
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

        if (requestCode != null) {
            val targetAlarmData = alarmDao.searchAlarmDataWithRequestCode(requestCode.toString())
            Log.d("ReceiverAlarmWorker", "ReceiverWorker Called!!")
            targetAlarmData.collectLatest { alarmDataList ->
                if (alarmDataList.isNotEmpty()){
                    Log.d("ReceiverAlarmWorker", "Called alarm data: $alarmDataList")
                    val todayWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
                    Log.d("ReceiverAlarmWorker", "today week: $todayWeek")

                    when (todayWeek) {
                        1 -> {
                            if (alarmDataList[0].Sun) {
                                openOnAlarmActivity(applicationContext)
                            }
                        }
                        2 -> {
                            if (alarmDataList[0].Mon) {
                                openOnAlarmActivity(applicationContext)
                            }
                        }
                        3 -> {
                            if (alarmDataList[0].Tue) {
                                openOnAlarmActivity(applicationContext)
                            }
                        }
                        4 -> {
                            if (alarmDataList[0].Wed) {
                                openOnAlarmActivity(applicationContext)
                            }
                        }
                        5 -> {
                            if (alarmDataList[0].Thu) {
                                openOnAlarmActivity(applicationContext)
                            }
                        }
                        6 -> {
                            if (alarmDataList[0].Fri) {
                                openOnAlarmActivity(applicationContext)
                            }
                        }
                        7 -> {
                            if (alarmDataList[0].Sat) {
                                openOnAlarmActivity(applicationContext)
                            }
                        }
                    }
                }
            }
        }
        return Result.success()
    }
}

fun openOnAlarmActivity(context: Context) {
    val onAlarmActivity = Intent(context, OnAlarmActivity::class.java)
    onAlarmActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    onAlarmActivity.putExtra("requestCode", requestCode)
    context.startActivity(onAlarmActivity)
}