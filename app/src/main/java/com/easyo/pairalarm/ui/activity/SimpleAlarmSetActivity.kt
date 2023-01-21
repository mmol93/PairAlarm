package com.easyo.pairalarm.ui.activity

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivitySimpleAlarmBinding
import com.easyo.pairalarm.extensions.clearKeyBoardFocus
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.ui.dialog.BellSelectDialogFragment
import com.easyo.pairalarm.util.*
import com.easyo.pairalarm.viewModel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*

@AndroidEntryPoint
class SimpleAlarmSetActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimpleAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bellSelectDialog = BellSelectDialogFragment()

        // UI 초기화
        lifecycleScope.launch {
            launch {
                alarmViewModel.getAlarmData(null).collectLatest {
                    binding.alarmData = it
                    alarmViewModel.currentAlarmBell.value = it.bell
                    alarmViewModel.currentAlarmMode.value = it.mode
                    Timber.d("selected alarmData: $it")
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
                // bellDialog에서 변경한 bellIndex를 갱신한다
                alarmViewModel.currentAlarmBell.collectLatest { bellIndex ->
                    if (binding.alarmData != null) {
                        binding.alarmData = binding.alarmData?.copy(bell = bellIndex)
                    }
                }
            }
        }

        // editText의 외부를 클릭했을 때는 키보드랑 Focus 제거하기
        binding.rootLayout.setOnClickListener {
            clearKeyBoardFocus(binding.rootLayout)
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
                binding.alarmData = seekBar?.progress?.let { binding.alarmData!!.copy(volume = it) }
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
                binding.alarmData =
                    binding.alarmData?.copy(mode = alarmViewModel.currentAlarmMode.value)
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
                when (binding.alarmData!!.vibration) {
                    0 -> {
                        binding.alarmData = binding.alarmData!!.copy(vibration = 1)
                    }
                    1 -> {
                        binding.alarmData = binding.alarmData!!.copy(vibration = 2)
                        AlarmAnimation.swing(this).start()
                    }
                    2 -> {
                        binding.alarmData = binding.alarmData!!.copy(vibration = 0)
                    }
                }
            }
        }

        // 볼륨 이미지를 클릭했을 때
        binding.imageVolume.setOnClickListener {
            if (binding.alarmData!!.volume > 0) {
                binding.alarmData = binding.alarmData!!.copy(volume = 0)
            } else {
                binding.alarmData = binding.alarmData!!.copy(volume = 100)
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

                val alarmData = makeAlarmData(
                    calendar = dateData,
                    alarmName = binding.alarmNameEditText.text.toString(),
                    alarmData = binding.alarmData!!
                )
                alarmViewModel.insertAlarmData(this, alarmData)

                finish()
            } else {
                makeToast(this, getString(R.string.toast_set_minimum_time))
            }
        }
    }
}
