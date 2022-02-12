package com.easyo.pairalarm.ui.activity

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityMakeAlarmBinding
import android.view.inputmethod.InputMethodManager
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.ui.dialog.BellSelect
import com.easyo.pairalarm.util.MakeAnimation
import com.easyo.pairalarm.util.makeToast
import com.easyo.pairalarm.util.setOnSingleClickExt
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NormalAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // numberPicker의 시간 부분 최대, 최소값 설정
        binding.numberPickerHour.maxValue = 12
        binding.numberPickerHour.minValue = 1

        // numberPicker의 분 부분 최대, 최소값 설정
        binding.numberPickerMin.maxValue = 59
        binding.numberPickerMin.minValue = 0

        // numberPicker에 오전 오후 텍스트 세팅..
        val arg1 = arrayOf(getString(R.string.alarmSet_AM), getString(R.string.alarmSet_PM))
        binding.numberPickerAMPM.minValue = 0
        binding.numberPickerAMPM.maxValue = arg1.size - 1
        binding.numberPickerAMPM.displayedValues = arg1

        var amPm = 0

        // alarmData의 requestCode 값이 0이면 새로운 알람 생성
        if (alarmViewModel.currentAlarmRequestCode.value == 0L){
            binding.saveButton.text = "update"
        }

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
                        if (it > 12) {
                            binding.numberPickerHour.value = (it - 12)
                        } else {
                            binding.numberPickerHour.value = it
                        }
                    }
                }
                launch {
                    // 분
                    alarmViewModel.currentAlarmMin.collectLatest {
                        binding.numberPickerMin.value = it
                    }
                }
                launch {
                    // 볼륨
                    alarmViewModel.currenAlarmVolume.collectLatest {
                        when (it) {
                            0 -> binding.imageVolume.setImageDrawable(getDrawable(R.drawable.volume_mute))
                            else -> binding.imageVolume.setImageDrawable(getDrawable(R.drawable.volume_icon))
                        }
                        binding.volumeSeekBar.progress = it
                    }
                }
                launch {
                    // 벨
                    alarmViewModel.currentAlarmBell.collectLatest {
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
                        Log.d("NormalAlarmActivity", "vibration changed")
                        when (it) {
                            0 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_no_vib))
                            1 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_vib_1))
                            2 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_vib_2))
                        }
                    }
                }
                launch {
                    // 월
                    alarmViewModel.currentAlarmMon.collectLatest {
                        binding.monButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 화
                    alarmViewModel.currentAlarmTue.collectLatest {
                        binding.tueButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 수
                    alarmViewModel.currentAlarmWed.collectLatest {
                        binding.wedButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 목
                    alarmViewModel.currentAlarmThu.collectLatest {
                        binding.thurButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 금
                    alarmViewModel.currentAlarmFri.collectLatest {
                        binding.friButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 토
                    alarmViewModel.currentAlarmSat.collectLatest {
                        binding.satButton.setStrokeColorInButton("Sat", it)
                    }
                }
                launch {
                    // 일
                    alarmViewModel.currentAlarmSun.collectLatest {
                        binding.sunButton.setStrokeColorInButton("Sun", it)
                    }
                }
                launch {
                    // Am PM
                    alarmViewModel.currenAlarmAmPm.collectLatest {
                        binding.numberPickerAMPM.value = alarmViewModel.currenAlarmAmPm.value
                    }
                }
            }
        }

        val makeAnimation = MakeAnimation()

        // editText의 외부를 클릭했을 때는 키보드랑 Focus 제거하기
        binding.rootLayout.setOnClickListener {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            binding.alarmNameEditTextLayout.clearFocus()
        }

        // AlarmBell 설정 버튼 눌렀을 때
        binding.selectBellButton.setOnSingleClickExt {
            val bellSelectDialog = BellSelect(this, alarmViewModel)
            bellSelectDialog.show()
        }

        // todo 데이터 저장 시 의도한대로 시간 데이터가 들어가는지 확인(recyclerView 만들고 나서)
        binding.numberPickerHour.setOnValueChangedListener { numberPicker, i, i2 ->
            alarmViewModel.currentAlarmHour.value = numberPicker.value
        }

        binding.numberPickerMin.setOnValueChangedListener { numberPicker, i, i2 ->
            alarmViewModel.currentAlarmMin.value = numberPicker.value
        }

        // todo 데이터 저장 시 의도한대로 시간 데이터가 들어가는지 확인(recyclerView 만들고 나서)
        binding.numberPickerAMPM.setOnValueChangedListener { numberPicker, i, i2 ->
            alarmViewModel.currenAlarmAmPm.value = numberPicker.value
        }

        // AlarmMode 설정 버튼 눌렀을 때
        binding.selectModeButton.setOnSingleClickExt {
            // ** 항목 선택 Dialog 설정
            val modeItem = arrayOf(
                getString(R.string.alarmSet_alarmModeItem1),
                getString(R.string.alarmSet_alarmModeItem2)
            )
            val builder = AlertDialog.Builder(this)

            builder.setTitle(getString(R.string.alarmSet_selectBellDialogTitle))
            builder.setSingleChoiceItems(modeItem, alarmViewModel.currentAlarmMode.value, null)
            builder.setNeutralButton(getString(R.string.cancel), null)

            builder.setPositiveButton(getString(R.string.ok)) { dialogInterface: DialogInterface, i: Int ->
                val alert = dialogInterface as AlertDialog
                val idx = alert.listView.checkedItemPosition
                // * 선택된 아이템의 position에 따라 행동 조건 넣기
                when (idx) {
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

        // save 버튼 눌렀을 때
        binding.saveButton.setOnSingleClickExt {
            // 요일을 하나라도 선택해야한다
            if (alarmViewModel.currentAlarmMon.value ||
                alarmViewModel.currentAlarmTue.value ||
                alarmViewModel.currentAlarmWed.value ||
                alarmViewModel.currentAlarmThu.value ||
                alarmViewModel.currentAlarmFri.value ||
                alarmViewModel.currentAlarmSat.value ||
                alarmViewModel.currentAlarmSun.value) {
                var hour = 0

                // 오전, 오후에 따라 hour의 값에 12를 더해주기
                if (alarmViewModel.currenAlarmAmPm.value == 0) {
                    hour = alarmViewModel.currentAlarmHour.value
                } else {
                    hour = alarmViewModel.currentAlarmHour.value + 12
                    // 24시는 0시로 설정되게 한다
                    if (hour == 24) {
                        hour = 0
                    }
                }

                // DB의 requestCode에 넣을 unique 수 생성
                var requestCode = 0L
                if (alarmViewModel.currentAlarmRequestCode.value == null) {
                    requestCode = System.currentTimeMillis()
                } else {
                    requestCode = alarmViewModel.currentAlarmRequestCode.value!!
                }

                // DB에 넣을 Data set
                val alarmData = AlarmData(
                    // autoGenerate가 true이기 때문에 null을 넣으면 알아서 값이 들어간다
                    id = null,
                    button = true,
                    Sun = alarmViewModel.currentAlarmSun.value,
                    Mon = alarmViewModel.currentAlarmMon.value,
                    Tue = alarmViewModel.currentAlarmTue.value,
                    Wed = alarmViewModel.currentAlarmWed.value,
                    Thu = alarmViewModel.currentAlarmThu.value,
                    Fri = alarmViewModel.currentAlarmFri.value,
                    Sat = alarmViewModel.currentAlarmSat.value,
                    hour = hour,
                    minute = binding.numberPickerMin.value,
                    volume = alarmViewModel.currenAlarmVolume.value,
                    bell = alarmViewModel.currentAlarmBell.value,
                    quick = false,
                    mode = alarmViewModel.currentAlarmMode.value,
                    vibration = alarmViewModel.currentAlarmVibration.value,
                    name = binding.alarmNameEditText.text.toString(),
                    requestCode = requestCode
                )

                // DB에 데이터 삽입
                alarmViewModel.insert(alarmData)

                // setting에 사용된 viewModel 변수들은 모두 초기화해야한다다

                finish()
                initCurrentAlarmData()
            } else {
                makeToast(this, getString(R.string.alarmSet_Toast))
            }
        }

        // cancel 버튼 눌렀을 때
        binding.cancelButton.setOnSingleClickExt {
            finish()
            initCurrentAlarmData()
        }

        // 진동 버튼을 눌렀을 때때
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
                        makeAnimation.swing(this).start()
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
            if (alarmViewModel.currenAlarmVolume.value > 0) {
                alarmViewModel.currenAlarmVolume.value = 0
            } else {
                alarmViewModel.currenAlarmVolume.value = 100
            }
        }

        // 월
        binding.monButton.apply {
            setOnClickListener {
                if (!alarmViewModel.currentAlarmMon.value) {
                    alarmViewModel.currentAlarmMon.value = true
                    // 점프 애니메이션
                    makeAnimation.jump(this).start()
                } else {
                    alarmViewModel.currentAlarmMon.value = false
                    // 털어내는 애니메이션
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 화
        binding.tueButton.apply {
            setOnClickListener {
                if (!alarmViewModel.currentAlarmTue.value) {
                    alarmViewModel.currentAlarmTue.value = true
                    makeAnimation.jump(this).start()
                } else {
                    alarmViewModel.currentAlarmTue.value = false
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 수
        binding.wedButton.apply {
            setOnClickListener {
                if (!alarmViewModel.currentAlarmWed.value) {
                    alarmViewModel.currentAlarmWed.value = true
                    makeAnimation.jump(this).start()
                } else {
                    alarmViewModel.currentAlarmWed.value = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 목
        binding.thurButton.apply {
            setOnClickListener {
                if (!alarmViewModel.currentAlarmThu.value) {
                    alarmViewModel.currentAlarmThu.value = true
                    makeAnimation.jump(this).start()
                } else {
                    alarmViewModel.currentAlarmThu.value = false
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 금
        binding.friButton.apply {
            setOnClickListener {
                if (!alarmViewModel.currentAlarmFri.value) {
                    alarmViewModel.currentAlarmFri.value = true
                    makeAnimation.jump(this).start()
                } else {
                    alarmViewModel.currentAlarmFri.value = false
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 토
        binding.satButton.apply {
            setOnClickListener {
                if (!alarmViewModel.currentAlarmSat.value) {
                    alarmViewModel.currentAlarmSat.value = true
                    makeAnimation.jump(this).start()
                } else {
                    alarmViewModel.currentAlarmSat.value = false
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 일
        binding.sunButton.apply {
            setOnClickListener {
                if (!alarmViewModel.currentAlarmSun.value) {
                    alarmViewModel.currentAlarmSun.value = true
                    makeAnimation.jump(this).start()
                } else {
                    alarmViewModel.currentAlarmSun.value = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        initCurrentAlarmData()
    }

    private fun MaterialButton.setStrokeColorInButton(week: String?, weekClicked: Boolean) {
        if (week == null) {
            if (weekClicked) {
                setStrokeColorResource(R.color.deep_yellow)
            } else {
                setStrokeColorResource(R.color.background)
            }
        } else if (week == "Sat") {
            if (weekClicked) {
                setStrokeColorResource(R.color.main_deepBlue)
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

    private fun initCurrentAlarmData() {
        alarmViewModel.currentAlarmData.value = AlarmData(
            id = null,
            button = true,
            Sun = false,
            Mon = false,
            Tue = false,
            Wed = false,
            Thu = false,
            Fri = false,
            Sat = false,
            vibration = 0,
            requestCode = 0,
            mode = 0,
            hour = 1,
            minute = 0,
            quick = false,
            volume = 100,
            bell = 0,
            name = ""
        )
    }
}