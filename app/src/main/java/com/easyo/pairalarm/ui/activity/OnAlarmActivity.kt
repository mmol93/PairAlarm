package com.easyo.pairalarm.ui.activity

import android.content.Context
import android.content.Intent
import android.os.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityOnAlarmBinding
import com.easyo.pairalarm.extensions.displayOn
import com.easyo.pairalarm.extensions.doShortVibrateOnce
import com.easyo.pairalarm.model.AlarmModeType
import com.easyo.pairalarm.model.CalculatorProblem
import com.easyo.pairalarm.service.AlarmForeground
import com.easyo.pairalarm.util.*
import com.easyo.pairalarm.viewModel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class OnAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()
    lateinit var calculatorProblem: CalculatorProblem
    private lateinit var vibrator: Vibrator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this

        vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else getSystemService(VIBRATOR_SERVICE) as Vibrator

        // 현재 화면이 자동으로 꺼지지 않게 유지 & 잠금화면에 액티비티 띄우기
        displayOn()

        val alarmCode = intent.getStringExtra(ALARM_CODE_TEXT)

        val backButtonCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
            }
        }
        this.onBackPressedDispatcher.addCallback(this, backButtonCallback)

        if (alarmCode != null) {
            val goesOffAlarmData = alarmViewModel.searchAlarmCode(alarmCode.toString())
            // 현재 시간이 계속 갱신되게한다
            val handler = Handler(Looper.getMainLooper())
            val handlerTask = object : Runnable {
                override fun run() {
                    handler.postDelayed(this, 3600)

                    binding.hour.setText(getCurrentHourDoubleDigitWithString())
                    binding.min.setText(getCurrentMinuteDoubleDigitWithString())
                }
            }

            handler.post(handlerTask)
            lifecycleScope.launch {
                goesOffAlarmData.first { currentAlarmData ->
                    Timber.d("goes off alarmData in OnAlarmActivity: $alarmCode")

                    binding.hour.setText(getCurrentHourDoubleDigitWithString())
                    binding.min.setText(getCurrentMinuteDoubleDigitWithString())
                    calculatorProblem = alarmViewModel.getRandomNumberForCalculator()
                    binding.calculatorProblem = calculatorProblem
                    binding.showCalculatorProblem = currentAlarmData.mode == AlarmModeType.CALCULATE.mode
                    binding.alarmName.text = currentAlarmData.name

                    if (currentAlarmData.quick) {
                        alarmViewModel.deleteAlarmData(currentAlarmData)
                    }
                    when(currentAlarmData.vibration) {
                        // 한 번만 짧게 진동
                        1 -> {
                            vibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE))
                        }
                        // 현재 액티비티가 보이지 않을 때까지 실시
                        2 -> {
                            // 1초 진동 -> 1초 휴식 -> 반복
                            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(500, 500), 0))
                        }
                    }

                    // 삭제하거나 변경된 알람들을 반영한다(Noti 등)
                    val serviceIntent = Intent(applicationContext, AlarmForeground::class.java)
                    startForegroundService(serviceIntent)
                    true
                }
            }

            // +10분 스누즈
            binding.tenMinutes.setOnClickListener {
                val calendar = Calendar.getInstance().apply {
                    add(Calendar.MINUTE, 10)
                }
                val addHour = calendar.get(Calendar.HOUR_OF_DAY)
                val addMinute = calendar.get(Calendar.MINUTE)
                setAlarmOnBroadcast(
                    this@OnAlarmActivity,
                    alarmCode.toInt(),
                    addHour,
                    addMinute
                )

                showShortToast(
                    this@OnAlarmActivity,
                    getString(R.string.toast_ten_minute_later)
                )

                handler.removeMessages(0)
                finish()
            }

            binding.ok.setOnClickListener {
                handler.removeMessages(0)
                cancelVibrate()
                finish()
            }

            lifecycleScope.launch {
                alarmViewModel.answer.collectLatest {
                    when {
                        it.isBlank() -> binding.problemAnswer.text = "???"
                        it == calculatorProblem.answer -> {
                            showShortToast(
                                this@OnAlarmActivity,
                                getString(R.string.toast_correct_calculator_answer)
                            )
                            // TODO: 정답 맞추면 dialog로 정답인거 알려주고 ok 누르면 액티비티랑 같이 꺼지게 하기
                            finish()
                        }
                        it.length > 3 -> {
                            alarmViewModel.answer.value = ""
                            doShortVibrateOnce()
                            showShortToast(
                                this@OnAlarmActivity,
                                getString(R.string.toast_wrong_calculator_answer)
                            )
                        }
                        else -> binding.problemAnswer.text = it
                    }
                }
            }

            // 숫자 버튼 클릭에 따른 행동 정의
            binding.oneButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "1"
            }
            binding.twoButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "2"
            }
            binding.threeButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "3"
            }
            binding.fourButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "4"
            }
            binding.fiveButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "5"
            }
            binding.sixButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "6"
            }
            binding.sevenButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "7"
            }
            binding.eightButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "8"
            }
            binding.nineButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "9"
            }
            binding.zeroButton.setOnClickListener {
                alarmViewModel.answer.value = alarmViewModel.answer.value + "0"
            }

            binding.deleteButton.setOnClickListener {
                alarmViewModel.answer.value.let {
                    if (it.isNotBlank()) {
                        alarmViewModel.answer.value = it.dropLast(1)
                    }
                }
            }

            binding.resetButton.setOnClickListener {
                alarmViewModel.answer.value = ""
            }

        } else {
            showShortToast(this, getString(R.string.on_alarm_error))
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancelVibrate()
    }

    private fun cancelVibrate() {
        runCatching {
            vibrator.cancel()
        }.onFailure {
            Timber.e("진동 취소 에러(아마 진동이 울리지 않고 있는 상태였음)")
        }
    }
}
