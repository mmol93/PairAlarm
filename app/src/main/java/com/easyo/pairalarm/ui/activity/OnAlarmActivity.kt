package com.easyo.pairalarm.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.work.WorkManager
import com.easyo.pairalarm.R
import com.easyo.pairalarm.alarm.setAlarm
import com.easyo.pairalarm.databinding.ActivityOnAlarmBinding
import com.easyo.pairalarm.util.getCurrentHourDoubleDigitWithString
import com.easyo.pairalarm.util.getCurrentMinuteDoubleDigitWithString
import com.easyo.pairalarm.viewModel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

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

            val goesOnAlarmData = alarmViewModel.searchRequestCode(requestCode.toString())
            lifecycleScope.launch {
                goesOnAlarmData.collectLatest { alarmDataList ->
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
                            finish()
                        }

                        // todo need to set 10 later function
                        binding.tenMinutes.setOnClickListener {
                            finish()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.on_alarm_error), Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun stopOnAlarmWorkManager() {
        WorkManager.getInstance(this).cancelUniqueWork("onAlarmActivity")
    }
}