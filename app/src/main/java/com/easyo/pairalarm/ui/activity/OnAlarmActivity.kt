package com.easyo.pairalarm.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.*
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityOnAlarmBinding
import com.easyo.pairalarm.extensions.displayOn
import com.easyo.pairalarm.util.*
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
        val alarmCode = intent.getStringExtra(ALARM_CODE_TEXT)

        if (alarmCode != null) {
            stopOnAlarmWorkManager()
            val goesOffAlarmData = alarmViewModel.searchAlarmCode(alarmCode.toString())
            // 현재 시간이 계속 갱신되게한다
            val handler = Handler(Looper.getMainLooper())
            val handlerTask = object : Runnable {
                override fun run() {
                    handler.postDelayed(this, 3600)

                    binding.hour.text = getCurrentHourDoubleDigitWithString()
                    binding.min.text = getCurrentMinuteDoubleDigitWithString()
                }
            }
            handler.post(handlerTask)
            lifecycleScope.launch {
                goesOffAlarmData.collectLatest { alarmData ->
                    // 현재 화면이 자동으로 꺼지지 않게 유지 & 잠금화면에 액티비티 띄우기
                    displayOn()

                    binding.hour.text = getCurrentHourDoubleDigitWithString()
                    binding.min.text = getCurrentMinuteDoubleDigitWithString()

                    binding.ok.setOnClickListener {
                        if (alarmData.quick) {
                            alarmViewModel.deleteAlarmData(this@OnAlarmActivity, alarmData)
                        } else {
                            setAlarm(
                                this@OnAlarmActivity,
                                alarmData.alarmCode.toInt(),
                                alarmData.hour,
                                alarmData.minute
                            )
                        }

                        val alarmTimeWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<NextAlarmWorker>().build()
                        WorkManager.getInstance(this@OnAlarmActivity)
                            .enqueueUniqueWork(
                                MAKE_ALARM_WORKER,
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
                            alarmData.alarmCode.toInt(),
                            addHour,
                            addMinute
                        )

                        makeToast(
                            this@OnAlarmActivity,
                            getString(R.string.toast_ten_minute_later)
                        )

                        handler.removeMessages(0)
                        finish()
                    }
                }
            }
        } else {
            makeToast(this, getString(R.string.on_alarm_error))
            finish()
        }
    }

    private fun stopOnAlarmWorkManager() {
        WorkManager.getInstance(this).cancelUniqueWork(OPEN_ALARM_ACTIVITY_WORKER)
    }
}
