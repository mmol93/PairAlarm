package com.easyo.pairalarm.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityNormalAlarmBinding
import com.easyo.pairalarm.extensions.clearKeyBoardFocus
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.extensions.showErrorSnackBar
import com.easyo.pairalarm.ui.dialog.BellSelectDialogFragment
import com.easyo.pairalarm.ui.dialog.SimpleDialog
import com.easyo.pairalarm.util.ALARM_CODE_TEXT
import com.easyo.pairalarm.util.AlarmAnimation
import com.easyo.pairalarm.util.getNewAlarmCode
import com.easyo.pairalarm.util.showShortToast
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class NormalAlarmSetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNormalAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()
    private val bellSelectDialog by lazy {
        BellSelectDialogFragment(alarmViewModel.currentAlarmBell.value) { bellIndex ->
            alarmViewModel.currentAlarmBell.value = bellIndex
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNormalAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.manualChangeForHour = false

        // alarmData를 사용하여 UI를 초기화
        var alarmCode = intent.getStringExtra(ALARM_CODE_TEXT)
        lifecycleScope.launch {
            alarmViewModel.getAlarmData(alarmCode).collect {
                binding.alarmData = it
                alarmViewModel.currentAlarmBell.value = it.bell
                alarmViewModel.currentAlarmMode.value = it.mode
                Timber.d("selected alarmData: $it")
            }
        }

        // numberPicker의 시간 부분 초기화
        binding.numberPickerHour.apply {
            maxValue = 12
            minValue = 1
            setOnValueChangedListener { _, _, newVal ->
                binding.alarmData?.let {
                    binding.manualChangeForHour = true
                    binding.alarmData = it.copy(hour = newVal)
                }
            }
        }

        // numberPicker의 분 부분 초기화
        binding.numberPickerMin.apply {
            maxValue = 59
            minValue = 0
            setOnValueChangedListener { _, _, newVal ->
                binding.alarmData?.let {
                    binding.manualChangeForHour = true
                    binding.alarmData = it.copy(minute = newVal)
                }
            }
        }

        // numberPicker에 오전 오후 부분 초기화
        val arg1 = arrayOf(getString(R.string.alarmSet_AM), getString(R.string.alarmSet_PM))
        binding.numberPickerAMPM.apply {
            maxValue = arg1.size - 1
            minValue = 0
            displayedValues = arg1
            setOnValueChangedListener { _, _, _ ->
                binding.manualChangeForHour = true
            }
        }

        // bellDialog에서 변경한 bellIndex를 갱신한다
        lifecycleScope.launch {
            alarmViewModel.currentAlarmBell.collectLatest { bellIndex ->
                if (binding.alarmData != null) {
                    binding.alarmData = binding.alarmData?.copy(bell = bellIndex)
                }
            }
        }

        // editText의 외부를 클릭했을 때는 키보드랑 Focus 제거하기
        binding.rootLayout.setOnClickListener {
            clearKeyBoardFocus(binding.rootLayout)
        }

        // AlarmBell 설정 버튼 눌렀을 때
        binding.selectBellButton.setOnSingleClickListener {
            bellSelectDialog.show(supportFragmentManager, null)
        }

        // AlarmMode 설정 버튼 눌렀을 때
        binding.selectModeButton.setOnSingleClickListener {
            // ** 항목 선택 Dialog 설정
            SimpleDialog.showAlarmModeDialog(
                this,
                clickedItemPosition = binding.alarmData!!.mode,
                positive = { dialogInterface ->
                    val alert = dialogInterface as AlertDialog
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
            )
        }

        // save 버튼 눌렀을 때
        binding.saveButton.setOnSingleClickListener {
            // 요일을 하나라도 선택해야한다
            if (binding.alarmData!!.Mon ||
                binding.alarmData!!.Tue ||
                binding.alarmData!!.Wed ||
                binding.alarmData!!.Thu ||
                binding.alarmData!!.Fri ||
                binding.alarmData!!.Sat ||
                binding.alarmData!!.Sun
            ) {
                val hour = when {
                    binding.numberPickerHour.value == 12 && binding.numberPickerAMPM.value == 1 -> 12
                    binding.numberPickerHour.value == 12 && binding.numberPickerAMPM.value == 0 -> 0
                    binding.numberPickerAMPM.value == 1 && binding.numberPickerHour.value + 12 < 24 ->
                        binding.numberPickerHour.value + 12
                    else -> binding.numberPickerHour.value
                }

                // 새로운 알람일 경우
                if (alarmCode == null) {
                    alarmCode = getNewAlarmCode()
                    // DB에 넣을 Data set
                    val alarmData = binding.alarmData!!.copy(
                        hour = hour,
                        minute = binding.numberPickerMin.value,
                        alarmCode = alarmCode!!,
                        name = binding.alarmNameEditText.text.toString()
                    )

                    // DB에 데이터 삽입
                    alarmViewModel.insertAlarmData(alarmData)
                    Timber.d("saved alarmData: $alarmData")
                }
                // 데이터를 수정 했을 경우
                else {
                    // DB에 넣을 Data set
                    val alarmData = binding.alarmData!!.copy(
                        hour = hour,
                        minute = binding.numberPickerMin.value,
                        alarmCode = alarmCode!!,
                        name = binding.alarmNameEditText.text.toString()
                    )
                    // DB 업데이트
                    alarmViewModel.updateAlarmData(alarmData.copy(alarmIsOn = true))
                    Timber.d("updated alarmData: $alarmData")
                }
                finish()
            } else {
                showShortToast(this, getString(R.string.alarmSet_Toast_week))
            }
        }

        // cancel 버튼 눌렀을 때
        binding.cancelButton.setOnSingleClickListener {
            finish()
        }

        // 진동 버튼을 눌렀을 때때
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

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                binding.alarmData = seekBar?.progress?.let { binding.alarmData!!.copy(volume = it) }
            }
        })

        // 월
        binding.monButton.apply {
            setOnClickListener {
                if (!binding.alarmData!!.Mon) {
                    binding.alarmData = binding.alarmData?.copy(Mon = true)
                    // 점프 애니메이션
                    AlarmAnimation.jump(this).start()
                } else {
                    binding.alarmData = binding.alarmData?.copy(Mon = false)
                    // 털어내는 애니메이션
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, binding.alarmData!!.Mon)
            }
        }

        // 화
        binding.tueButton.apply {
            setOnClickListener {
                if (!binding.alarmData!!.Tue) {
                    binding.alarmData = binding.alarmData?.copy(Tue = true)
                    AlarmAnimation.jump(this).start()
                } else {
                    binding.alarmData = binding.alarmData?.copy(Tue = false)
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, binding.alarmData!!.Tue)
            }
        }

        // 수
        binding.wedButton.apply {
            setOnClickListener {
                if (!binding.alarmData!!.Wed) {
                    binding.alarmData = binding.alarmData?.copy(Wed = true)
                    AlarmAnimation.jump(this).start()
                } else {
                    binding.alarmData = binding.alarmData?.copy(Wed = false)
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, binding.alarmData!!.Wed)
            }
        }

        // 목
        binding.thurButton.apply {
            setOnClickListener {
                if (!binding.alarmData!!.Thu) {
                    binding.alarmData = binding.alarmData?.copy(Thu = true)
                    AlarmAnimation.jump(this).start()
                } else {
                    binding.alarmData = binding.alarmData?.copy(Thu = false)
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, binding.alarmData!!.Thu)
            }
        }

        // 금
        binding.friButton.apply {
            setOnClickListener {
                if (!binding.alarmData!!.Fri) {
                    binding.alarmData = binding.alarmData?.copy(Fri = true)
                    AlarmAnimation.jump(this).start()
                } else {
                    binding.alarmData = binding.alarmData?.copy(Fri = false)
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, binding.alarmData!!.Fri)
            }
        }

        // 토
        binding.satButton.apply {
            setOnClickListener {
                if (!binding.alarmData!!.Sat) {
                    binding.alarmData = binding.alarmData?.copy(Sat = true)
                    AlarmAnimation.jump(this).start()
                } else {
                    binding.alarmData = binding.alarmData?.copy(Sat = false)
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, binding.alarmData!!.Sat)
            }
        }

        // 일
        binding.sunButton.apply {
            setOnClickListener {
                if (!binding.alarmData!!.Sun) {
                    binding.alarmData = binding.alarmData?.copy(Sun = true)
                    AlarmAnimation.jump(this).start()
                } else {
                    binding.alarmData = binding.alarmData?.copy(Sun = false)
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, binding.alarmData!!.Sun)
            }
        }

        binding.alarmNameEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                setTextForAlarmName(s.toString())
            }
        })
        binding.alarmNameEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                clearKeyBoardFocus(binding.rootLayout)
            }
        }

        alarmViewModel.failure.observe(this) {
            showErrorSnackBar(binding.root, it)
        }
    }

    private fun setTextForAlarmName(newText: String) {
        binding.alarmData = binding.alarmData?.copy(name = newText)
    }

    private fun MaterialButton.setStrokeColorInButton(week: String?, weekClicked: Boolean) {
        if (week == null) {
            if (weekClicked) {
                this.setStrokeColorResource(R.color.deep_yellow)
            } else {
                this.setStrokeColorResource(R.color.background)
            }
        } else if (week == "Sat") {
            if (weekClicked) {
                setStrokeColorResource(R.color.light_blue)
            } else {
                setStrokeColorResource(R.color.background)
            }
        } else if (week == "Sun") {
            if (weekClicked) {
                setStrokeColorResource(R.color.red)
            } else {
                setStrokeColorResource(R.color.background)
            }
        }
    }
}
