package com.easyo.pairalarm.ui.activity

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivitySimpleAlarmBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.ui.dialog.BellSelectDialogFragment
import com.easyo.pairalarm.util.*
import com.easyo.pairalarm.viewModel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class SimpleAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimpleAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bellSelectDialog = BellSelectDialogFragment()

        // UI 초기화
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    // 알람 이름
                    alarmViewModel.currentAlarmName.collectLatest {
                        binding.alarmNameEditText.setText(it)
                    }
                }
                launch {
                    // 시간
                    alarmViewModel.currentAlarmHour.collectLatest {
                        if (it >= 10) {
                            binding.plusHourText.text = it.toString()
                        } else {
                            binding.plusHourText.text = "0$it"
                        }
                    }
                }
                launch {
                    // 분
                    alarmViewModel.currentAlarmMin.collectLatest {
                        if (it >= 60) {
                            alarmViewModel.currentAlarmMin.value -= 60
                            alarmViewModel.currentAlarmHour.value += 1
                        }
                        if (it >= 10) {
                            binding.plusMinText.text = it.toString()
                        } else {
                            binding.plusMinText.text = "0$it"
                        }

                    }
                }
                launch {
                    // 볼륨
                    alarmViewModel.currentAlarmVolume.collectLatest {
                        binding.volumeSeekBar.progress = it
                    }
                }
                launch {
                    // 벨
                    alarmViewModel.currentAlarmBell.collectLatest {
                        Log.d(this@SimpleAlarmActivity.javaClass.simpleName, "bellIndex: $it")
                        when (it) {
                            0 -> binding.textCurrentBell.text =
                                getString(R.string.bellType_Normal_Walking)
                            1 -> binding.textCurrentBell.text =
                                getString(R.string.bellType_Normal_PianoMan)
                            2 -> binding.textCurrentBell.text =
                                getString(R.string.bellType_Normal_Happy)
                            3 -> binding.textCurrentBell.text =
                                getString(R.string.bellType_Normal_Lonely)
                        }
                    }
                }
                launch {
                    // 알람 모드
                    alarmViewModel.currentAlarmMode.collectLatest {
                        when (it) {
                            0 -> binding.textCurrentMode.text =
                                getString(R.string.alarmSet_alarmModeItem1)
                            1 -> binding.textCurrentMode.text =
                                getString(R.string.alarmSet_alarmModeItem2)
                        }
                    }
                }
                launch {
                    // 진동
                    alarmViewModel.currentAlarmVibration.collectLatest {
                        when (it) {
                            0 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_no_vib))
                            1 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_vib_1))
                            2 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_vib_2))
                        }
                    }
                }
            }
        }

        // editText의 외부를 클릭했을 때는 키보드랑 Focus 제거하기
        binding.rootLayout.setOnClickListener {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            binding.alarmNameEditTextLayout.clearFocus()
        }

        // 분 +에 대한 조작
        binding.min60Button.setOnClickListener {
            alarmViewModel.currentAlarmHour.value += 1
        }
        binding.min30Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 30
        }
        binding.min15Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 15
        }
        binding.min10Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 10
        }
        binding.min5Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 5
        }
        binding.min1Button.setOnClickListener {
            alarmViewModel.currentAlarmMin.value += 1
        }

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                alarmViewModel.currentAlarmVolume.value = seekBar!!.progress
            }
        })

        // 모드 버튼을 눌렀을 때
        binding.selectModeButton.setOnSingleClickListener {
            // ** 항목 선택 Dialog 설정
            val modeItem = arrayOf(
                getString(R.string.alarmSet_alarmModeItem1),
                getString(R.string.alarmSet_alarmModeItem2)
            )
            val builder = AlertDialog.Builder(this)

            builder.setTitle(getString(R.string.alarmSet_selectBellDialogTitle))
            builder.setSingleChoiceItems(
                modeItem,
                alarmViewModel.currentAlarmMode.value,
                null
            )
            builder.setNeutralButton(getString(R.string.cancel), null)

            builder.setPositiveButton(getString(R.string.ok)) { dialogInterface: DialogInterface, _: Int ->
                val alert = dialogInterface as AlertDialog
                // * 선택된 아이템의 position에 따라 행동 조건 넣기
                when (alert.listView.checkedItemPosition) {
                    // Normal 클릭 시
                    0 -> {
                        alarmViewModel.currentAlarmMode.value = 0
                    }
                    // Calculate 클릭 시
                    1 -> {
                        alarmViewModel.currentAlarmMode.value = 1
                    }
                }
            }
            builder.show()
        }

        // AlarmBell 설정 버튼 눌렀을 때
        binding.selectBellButton.setOnSingleClickListener {
            bellSelectDialog.show(supportFragmentManager, null)
        }

        // 진동 버튼을 눌렀을 때
        binding.imageVibration.apply {
            setOnClickListener {
                when (alarmViewModel.currentAlarmVibration.value) {
                    0 -> {
                        alarmViewModel.currentAlarmVibration.value = 1
                    }
                    1 -> {
                        lifecycleScope.launchWhenStarted {
                            alarmViewModel.currentAlarmVibration.emit(2)
                        }
                        AlarmAnimation.swing(this).start()
                    }
                    2 -> {
                        lifecycleScope.launchWhenStarted {
                            alarmViewModel.currentAlarmVibration.emit(0)
                        }
                    }
                }
            }
        }

        // 볼륨 이미지를 클릭했을 때
        binding.imageVolume.setOnClickListener {
            if (alarmViewModel.currentAlarmVolume.value > 0) {
                alarmViewModel.currentAlarmVolume.value = 0
            } else {
                alarmViewModel.currentAlarmVolume.value = 100
            }
        }

        binding.cancelButton.setOnClickListener {
            finish()
        }

        binding.saveButton.setOnSingleClickListener {
            if (alarmViewModel.currentAlarmHour.value > 0 || alarmViewModel.currentAlarmMin.value > 0) {
                val dateData = getAddedTime(
                    hour = alarmViewModel.currentAlarmHour.value,
                    min = alarmViewModel.currentAlarmMin.value
                )

                val alarmData = makeAlarmData(dateData, binding.alarmNameEditText.text.toString(), alarmViewModel)
                alarmViewModel.insertAlarmData(alarmData)

                val setHour = dateData.get(Calendar.HOUR_OF_DAY)
                val setMin = dateData.get(Calendar.MINUTE)

                val alarmCode = getNewAlarmCode()

                setAlarm(this, alarmCode.toInt(), setHour, setMin)

                finish()
            } else {
                makeToast(this, getString(R.string.toast_set_minimum_time))
            }
        }
    }
}
