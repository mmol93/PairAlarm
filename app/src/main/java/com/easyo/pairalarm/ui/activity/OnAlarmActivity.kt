package com.easyo.pairalarm.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.easyo.pairalarm.R
import com.easyo.pairalarm.alarm.setAlarm
import com.easyo.pairalarm.databinding.ActivityOnAlarmBinding
import com.easyo.pairalarm.util.getCurrentHourDoubleDigitWithString
import com.easyo.pairalarm.util.getCurrentMinuteDoubleDigitWithString
import com.easyo.pairalarm.util.makeToast
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.easyo.pairalarm.worker.NextAlarmWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class OnAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val requestCode = intent.getStringExtra("requestCode")

        if (requestCode != null) {
            stopOnAlarmWorkManager()
            val goesOffAlarmData = alarmViewModel.searchRequestCode(requestCode.toString())
            lifecycleScope.launch {
                goesOffAlarmData.collectLatest { alarmDataList ->
                    if (alarmDataList.isNotEmpty()) {
                        binding.hour.text = getCurrentHourDoubleDigitWithString()
                        binding.min.text = getCurrentMinuteDoubleDigitWithString()

                        binding.ok.setOnClickListener {
                            if (alarmDataList[0].quick) {
                                alarmViewModel.delete(alarmDataList[0])
                            } else {
                                setAlarm(
                                    this@OnAlarmActivity,
                                    alarmDataList[0].requestCode.toInt(),
                                    alarmDataList[0].hour,
                                    alarmDataList[0].minute
                                )
                            }

                            val alarmTimeWorkRequest: WorkRequest =
                                OneTimeWorkRequestBuilder<NextAlarmWorker>().build()
                            WorkManager.getInstance(this@OnAlarmActivity)
                                .enqueueUniqueWork(
                                    "makeNotification",
                                    ExistingWorkPolicy.KEEP,
                                    alarmTimeWorkRequest as OneTimeWorkRequest
                                )

                            finish()
                        }

                        binding.tenMinutes.setOnClickListener {
                            val calendar = Calendar.getInstance().apply {
                                add(Calendar.MINUTE, 10)
                            }
                            val addHour = calendar.get(Calendar.HOUR_OF_DAY)
                            val addMinute = calendar.get(Calendar.MINUTE)
                            setAlarm(
                                this@OnAlarmActivity,
                                alarmDataList[0].requestCode.toInt(),
                                addHour,
                                addMinute
                            )

                            makeToast(
                                this@OnAlarmActivity,
                                getString(R.string.toast_ten_minute_later)
                            )

                            finish()
                        }
                    }
                }
            }
        } else {
            makeToast(this, getString(R.string.on_alarm_error))
            finish()
        }
    }

    private fun stopOnAlarmWorkManager() {
        WorkManager.getInstance(this).cancelUniqueWork("onAlarmActivity")
    }
}
