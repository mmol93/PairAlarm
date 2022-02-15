package com.easyo.pairalarm.ui.activity

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityMakeAlarmBinding
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.ui.dialog.BellSelect
import com.easyo.pairalarm.util.MakeAnimation
import com.easyo.pairalarm.util.initCurrentAlarmData
import com.easyo.pairalarm.util.makeToast
import com.easyo.pairalarm.util.setOnSingleClickExt
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class NormalAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeAlarmBinding

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

        Log.d("NormalAlarmActivity", "alarmRequestCode: ${AppClass.alarmViewModel.currentAlarmRequestCode.value} ")

        // alarmData의 requestCode 값이 0이면 새로운 알람 생성
        if (AppClass.alarmViewModel.currentAlarmRequestCode.value == null || AppClass.alarmViewModel.currentAlarmRequestCode.value == 0L) {
            binding.saveButton.text = "save"
        }

        // UI 초기화
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    // 알람 이름
                    AppClass.alarmViewModel.currentAlarmName.collectLatest {
                        binding.alarmNameEditText.setText(it)
                    }
                }
                launch {
                    // 시간
                    AppClass.alarmViewModel.currentAlarmHour.collectLatest {
                        if (it > 12) {
                            binding.numberPickerHour.value = (it - 12)
                        } else {
                            binding.numberPickerHour.value = it
                        }
                    }
                }
                launch {
                    // 분
                    AppClass.alarmViewModel.currentAlarmMin.collectLatest {
                        binding.numberPickerMin.value = it
                    }
                }
                launch {
                    // 볼륨
                    AppClass.alarmViewModel.currentAlarmVolume.collectLatest {
                        when (it) {
                            0 -> binding.imageVolume.setImageDrawable(getDrawable(R.drawable.volume_mute))
                            else -> binding.imageVolume.setImageDrawable(getDrawable(R.drawable.volume_icon))
                        }
                        binding.volumeSeekBar.progress = it
                    }
                }
                launch {
                    // 벨
                    AppClass.alarmViewModel.currentAlarmBell.collectLatest {
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
                    AppClass.alarmViewModel.currentAlarmMode.collectLatest {
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
                    AppClass.alarmViewModel.currentAlarmVibration.collectLatest {
                        when (it) {
                            0 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_no_vib))
                            1 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_vib_1))
                            2 -> binding.imageVibration.setImageDrawable(getDrawable(R.drawable.ic_vib_2))
                        }
                    }
                }
                launch {
                    // 월
                    AppClass.alarmViewModel.currentAlarmMon.collectLatest {
                        binding.monButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 화
                    AppClass.alarmViewModel.currentAlarmTue.collectLatest {
                        binding.tueButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 수
                    AppClass.alarmViewModel.currentAlarmWed.collectLatest {
                        binding.wedButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 목
                    AppClass.alarmViewModel.currentAlarmThu.collectLatest {
                        binding.thurButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 금
                    AppClass.alarmViewModel.currentAlarmFri.collectLatest {
                        binding.friButton.setStrokeColorInButton(null, it)
                    }
                }
                launch {
                    // 토
                    AppClass.alarmViewModel.currentAlarmSat.collectLatest {
                        binding.satButton.setStrokeColorInButton("Sat", it)
                    }
                }
                launch {
                    // 일
                    AppClass.alarmViewModel.currentAlarmSun.collectLatest {
                        binding.sunButton.setStrokeColorInButton("Sun", it)
                    }
                }
                launch {
                    // Am PM
                    AppClass.alarmViewModel.currentAlarmAmPm.collectLatest {
                        binding.numberPickerAMPM.value =
                            AppClass.alarmViewModel.currentAlarmAmPm.value
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
            val bellSelectDialog = BellSelect(this, AppClass.alarmViewModel)
            bellSelectDialog.show()
        }

        binding.numberPickerHour.setOnValueChangedListener { numberPicker, i, i2 ->
            AppClass.alarmViewModel.currentAlarmHour.value = numberPicker.value
        }

        binding.numberPickerMin.setOnValueChangedListener { numberPicker, i, i2 ->
            AppClass.alarmViewModel.currentAlarmMin.value = numberPicker.value
        }

        binding.numberPickerAMPM.setOnValueChangedListener { numberPicker, i, i2 ->
            AppClass.alarmViewModel.currentAlarmAmPm.value = numberPicker.value
        }

        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                AppClass.alarmViewModel.currentAlarmVolume.value = seekBar!!.progress
            }
        })

        // AlarmMode 설정 버튼 눌렀을 때
        binding.selectModeButton.setOnSingleClickExt {
            // ** 항목 선택 Dialog 설정
            val modeItem = arrayOf(
                getString(R.string.alarmSet_alarmModeItem1),
                getString(R.string.alarmSet_alarmModeItem2)
            )
            val builder = AlertDialog.Builder(this)

            builder.setTitle(getString(R.string.alarmSet_selectBellDialogTitle))
            builder.setSingleChoiceItems(
                modeItem,
                AppClass.alarmViewModel.currentAlarmMode.value,
                null
            )
            builder.setNeutralButton(getString(R.string.cancel), null)

            builder.setPositiveButton(getString(R.string.ok)) { dialogInterface: DialogInterface, i: Int ->
                val alert = dialogInterface as AlertDialog
                val idx = alert.listView.checkedItemPosition
                // * 선택된 아이템의 position에 따라 행동 조건 넣기
                when (idx) {
                    // Normal 클릭 시
                    0 -> {
                        AppClass.alarmViewModel.currentAlarmMode.value = 0
                    }
                    // Calculate 클릭 시
                    1 -> {
                        AppClass.alarmViewModel.currentAlarmMode.value = 1
                    }
                }
            }
            builder.show()
        }

        // save 버튼 눌렀을 때
        binding.saveButton.setOnSingleClickExt {
            // 요일을 하나라도 선택해야한다
            if (AppClass.alarmViewModel.currentAlarmMon.value ||
                AppClass.alarmViewModel.currentAlarmTue.value ||
                AppClass.alarmViewModel.currentAlarmWed.value ||
                AppClass.alarmViewModel.currentAlarmThu.value ||
                AppClass.alarmViewModel.currentAlarmFri.value ||
                AppClass.alarmViewModel.currentAlarmSat.value ||
                AppClass.alarmViewModel.currentAlarmSun.value
            ) {
                var hour = 0

                // 오전, 오후에 따라 hour의 값에 12를 더해주기
                if (AppClass.alarmViewModel.currentAlarmAmPm.value == 1 && AppClass.alarmViewModel.currentAlarmHour.value + 12 <= 24){
                    hour = AppClass.alarmViewModel.currentAlarmHour.value + 12
                    // 24시는 0시로 설정되게 한다
                    if (hour == 24) {
                        hour = 0
                    }
                }else{
                    hour = AppClass.alarmViewModel.currentAlarmHour.value
                }

                // DB의 requestCode에 넣을 unique 수 생성
                var requestCode = 0L
                if (AppClass.alarmViewModel.currentAlarmRequestCode.value == 0L ||
                    AppClass.alarmViewModel.currentAlarmRequestCode.value == null) {
                    requestCode = System.currentTimeMillis()

                    // DB에 넣을 Data set
                    val alarmData = AlarmData(
                        // autoGenerate가 true이기 때문에 null을 넣으면 알아서 값이 들어간다
                        id = null,
                        button = true,
                        Sun = AppClass.alarmViewModel.currentAlarmSun.value,
                        Mon = AppClass.alarmViewModel.currentAlarmMon.value,
                        Tue = AppClass.alarmViewModel.currentAlarmTue.value,
                        Wed = AppClass.alarmViewModel.currentAlarmWed.value,
                        Thu = AppClass.alarmViewModel.currentAlarmThu.value,
                        Fri = AppClass.alarmViewModel.currentAlarmFri.value,
                        Sat = AppClass.alarmViewModel.currentAlarmSat.value,
                        hour = hour,
                        minute = binding.numberPickerMin.value,
                        volume = AppClass.alarmViewModel.currentAlarmVolume.value,
                        bell = AppClass.alarmViewModel.currentAlarmBell.value,
                        quick = false,
                        mode = AppClass.alarmViewModel.currentAlarmMode.value,
                        vibration = AppClass.alarmViewModel.currentAlarmVibration.value,
                        name = binding.alarmNameEditText.text.toString(),
                        requestCode = requestCode
                    )

                    // DB에 데이터 삽입
                    AppClass.alarmViewModel.insert(alarmData)
                }
                // 데이터를 수정
                else {
                    requestCode = AppClass.alarmViewModel.currentAlarmRequestCode.value!!

                    val alarmData = AlarmData(
                        // autoGenerate가 true이기 때문에 null을 넣으면 알아서 값이 들어간다
                        id = AppClass.alarmViewModel.currentAlarmId.value,
                        button = true,
                        Sun = AppClass.alarmViewModel.currentAlarmSun.value,
                        Mon = AppClass.alarmViewModel.currentAlarmMon.value,
                        Tue = AppClass.alarmViewModel.currentAlarmTue.value,
                        Wed = AppClass.alarmViewModel.currentAlarmWed.value,
                        Thu = AppClass.alarmViewModel.currentAlarmThu.value,
                        Fri = AppClass.alarmViewModel.currentAlarmFri.value,
                        Sat = AppClass.alarmViewModel.currentAlarmSat.value,
                        hour = hour,
                        minute = binding.numberPickerMin.value,
                        volume = AppClass.alarmViewModel.currentAlarmVolume.value,
                        bell = AppClass.alarmViewModel.currentAlarmBell.value,
                        quick = false,
                        mode = AppClass.alarmViewModel.currentAlarmMode.value,
                        vibration = AppClass.alarmViewModel.currentAlarmVibration.value,
                        name = binding.alarmNameEditText.text.toString(),
                        requestCode = requestCode
                    )

                    // DB 업데이트
                    AppClass.alarmViewModel.update(alarmData)
                }

                finish()
                // setting에 사용된 viewModel 변수들은 모두 초기화해야한다
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
                when (AppClass.alarmViewModel.currentAlarmVibration.value) {
                    0 -> {
                        AppClass.alarmViewModel.currentAlarmVibration.value = 1
                    }
                    1 -> {
                        lifecycleScope.launchWhenStarted {
                            AppClass.alarmViewModel.currentAlarmVibration.emit(2)
                        }
                        makeAnimation.swing(this).start()
                    }
                    2 -> {
                        lifecycleScope.launchWhenStarted {
                            AppClass.alarmViewModel.currentAlarmVibration.emit(0)
                        }
                    }
                }
            }
        }

        // 볼륨 이미지를 클릭했을 때
        binding.imageVolume.setOnClickListener {
            if (AppClass.alarmViewModel.currentAlarmVolume.value > 0) {
                AppClass.alarmViewModel.currentAlarmVolume.value = 0
            } else {
                AppClass.alarmViewModel.currentAlarmVolume.value = 100
            }
        }

        // 월
        binding.monButton.apply {
            setOnClickListener {
                if (!AppClass.alarmViewModel.currentAlarmMon.value) {
                    AppClass.alarmViewModel.currentAlarmMon.value = true
                    // 점프 애니메이션
                    makeAnimation.jump(this).start()
                } else {
                    AppClass.alarmViewModel.currentAlarmMon.value = false
                    // 털어내는 애니메이션
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 화
        binding.tueButton.apply {
            setOnClickListener {
                if (!AppClass.alarmViewModel.currentAlarmTue.value) {
                    AppClass.alarmViewModel.currentAlarmTue.value = true
                    makeAnimation.jump(this).start()
                } else {
                    AppClass.alarmViewModel.currentAlarmTue.value = false
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 수
        binding.wedButton.apply {
            setOnClickListener {
                if (!AppClass.alarmViewModel.currentAlarmWed.value) {
                    AppClass.alarmViewModel.currentAlarmWed.value = true
                    makeAnimation.jump(this).start()
                } else {
                    AppClass.alarmViewModel.currentAlarmWed.value = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 목
        binding.thurButton.apply {
            setOnClickListener {
                if (!AppClass.alarmViewModel.currentAlarmThu.value) {
                    AppClass.alarmViewModel.currentAlarmThu.value = true
                    makeAnimation.jump(this).start()
                } else {
                    AppClass.alarmViewModel.currentAlarmThu.value = false
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 금
        binding.friButton.apply {
            setOnClickListener {
                if (!AppClass.alarmViewModel.currentAlarmFri.value) {
                    AppClass.alarmViewModel.currentAlarmFri.value = true
                    makeAnimation.jump(this).start()
                } else {
                    AppClass.alarmViewModel.currentAlarmFri.value = false
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 토
        binding.satButton.apply {
            setOnClickListener {
                if (!AppClass.alarmViewModel.currentAlarmSat.value) {
                    AppClass.alarmViewModel.currentAlarmSat.value = true
                    makeAnimation.jump(this).start()
                } else {
                    AppClass.alarmViewModel.currentAlarmSat.value = false
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 일
        binding.sunButton.apply {
            setOnClickListener {
                if (!AppClass.alarmViewModel.currentAlarmSun.value) {
                    AppClass.alarmViewModel.currentAlarmSun.value = true
                    makeAnimation.jump(this).start()
                } else {
                    AppClass.alarmViewModel.currentAlarmSun.value = false
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