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
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.alarm.setAlarm
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.ui.dialog.BellSelect
import com.easyo.pairalarm.util.*
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

@AndroidEntryPoint
class NormalAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()
    private lateinit var currentAlarmDataList: Flow<List<AlarmData>>
    private lateinit var currentAlarmData: AlarmData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val appClass = AppClass()

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

        Log.d(
            "NormalAlarmActivity",
            "alarmRequestCode: ${AppClass.alarmViewModel.currentAlarmRequestCode.value} "
        )

        // alarmData의 requestCode 값이 0이면 새로운 알람 생성
        if (AppClass.alarmViewModel.currentAlarmRequestCode.value == null) {
            binding.saveButton.text = getString(R.string.save)
        }

        val requestCode = intent.getStringExtra("requestCode")
        currentAlarmDataList = if (requestCode != null){
            val clickedAlarmData = alarmViewModel.searchRequestCode(requestCode)
            clickedAlarmData
        }else{
            initCurrentAlarmData()
        }

        lifecycleScope.launch {
            currentAlarmDataList.collectLatest {
                it[0].let {alarmData ->
                    currentAlarmData = alarmData
                    Log.d("NormalAlarmActivity", "alarmData: $alarmData")

                    binding.alarmNameEditText.setText(alarmData.name)

                    if (alarmData.hour > 12){
                        binding.numberPickerHour.value = alarmData.hour - 12
                        binding.numberPickerAMPM.value = 1
                    }else{
                        binding.numberPickerHour.value = alarmData.hour
                        binding.numberPickerAMPM.value = 0
                    }

                    binding.numberPickerMin.value = alarmData.minute

                    if (alarmData.volume == 0){
                        binding.imageVolume.setImageDrawable(AppCompatResources.getDrawable(this@NormalAlarmActivity, R.drawable.volume_mute))
                    }else{
                        binding.imageVolume.setImageDrawable(AppCompatResources.getDrawable(this@NormalAlarmActivity, R.drawable.volume_icon))
                    }

                    when(alarmData.bell){
                        0 -> binding.textCurrentBell.text =
                            getString(R.string.bellType_Normal_Walking)
                        1 -> binding.textCurrentBell.text =
                            getString(R.string.bellType_Normal_PianoMan)
                        2 -> binding.textCurrentBell.text =
                            getString(R.string.bellType_Normal_Happy)
                        3 -> binding.textCurrentBell.text =
                            getString(R.string.bellType_Normal_Lonely)
                    }

                    when(alarmData.mode){
                        0 -> binding.textCurrentMode.text =
                            getString(R.string.alarmSet_alarmModeItem1)
                        1 -> binding.textCurrentMode.text =
                            getString(R.string.alarmSet_alarmModeItem2)
                    }

                    when(alarmData.vibration){
                        0 -> binding.imageVibration.setImageDrawable(AppCompatResources.getDrawable(this@NormalAlarmActivity, R.drawable.ic_no_vib))
                        1 -> binding.imageVibration.setImageDrawable(AppCompatResources.getDrawable(this@NormalAlarmActivity, R.drawable.ic_vib_1))
                        2 -> binding.imageVibration.setImageDrawable(AppCompatResources.getDrawable(this@NormalAlarmActivity, R.drawable.ic_vib_2))
                    }

                    binding.monButton.setStrokeColorInButton(null, alarmData.Mon)
                    binding.tueButton.setStrokeColorInButton(null, alarmData.Tue)
                    binding.wedButton.setStrokeColorInButton(null, alarmData.Wed)
                    binding.thurButton.setStrokeColorInButton(null, alarmData.Thu)
                    binding.friButton.setStrokeColorInButton(null, alarmData.Fri)
                    binding.satButton.setStrokeColorInButton("Sat", alarmData.Sat)
                    binding.sunButton.setStrokeColorInButton("Sun", alarmData.Sun)
                }
            }
        }

        val makeAnimation = AlarmAnimation()

        // editText의 외부를 클릭했을 때는 키보드랑 Focus 제거하기
        binding.rootLayout.setOnClickListener {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            binding.alarmNameEditTextLayout.clearFocus()
        }

        // AlarmBell 설정 버튼 눌렀을 때
        binding.selectBellButton.setOnSingleClickExt {
            val bellSelectDialog = BellSelect(this, alarmViewModel, null)
            bellSelectDialog.show()
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
            builder.setSingleChoiceItems(
                modeItem,
                AppClass.alarmViewModel.currentAlarmMode.value,
                null
            )
            builder.setNeutralButton(getString(R.string.cancel), null)

            builder.setPositiveButton(getString(R.string.ok)) { dialogInterface: DialogInterface, _: Int ->
                val alert = dialogInterface as AlertDialog
                // * 선택된 아이템의 position에 따라 행동 조건 넣기
                when (alert.listView.checkedItemPosition) {
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
            if (AppClass.alarmViewModel.currentAlarmTue.value ||
                AppClass.alarmViewModel.currentAlarmTue.value ||
                AppClass.alarmViewModel.currentAlarmWed.value ||
                AppClass.alarmViewModel.currentAlarmThu.value ||
                AppClass.alarmViewModel.currentAlarmFri.value ||
                AppClass.alarmViewModel.currentAlarmSat.value ||
                AppClass.alarmViewModel.currentAlarmSun.value
            ) {
                var hour: Int

                // 오전, 오후에 따라 hour의 값에 12를 더해주기
                if (binding.numberPickerHour.value == 12 && binding.numberPickerAMPM.value == 1){
                    hour = 12
                }else if(binding.numberPickerHour.value == 12 && binding.numberPickerAMPM.value == 0){
                    hour = 0
                }else if (binding.numberPickerAMPM.value == 1 && binding.numberPickerHour.value + 12 <= 24){
                    hour = binding.numberPickerHour.value + 12
                }else{
                    hour = binding.numberPickerHour.value
                }

                // DB의 requestCode에 넣을 unique 수 생성
                val requestCode: String

                // currentAlarmRequestCode를 보고 새로운 알람 생성인지 수정인지 판단
                if (AppClass.alarmViewModel.currentAlarmRequestCode.value == null
                ) {
                    val calendar = Calendar.getInstance()
                    val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
                    val currentHour = calendar.get(Calendar.HOUR_OF_DAY)
                    val currentMin = calendar.get(Calendar.MINUTE)
                    val currentSecond = calendar.get(Calendar.SECOND)

                    requestCode = currentDay.toString() + currentHour.toString() +
                            currentMin.toString() + currentSecond.toString()

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

                    // 브로드캐스트에 알람 예약하기
                    setAlarm(this, requestCode.toInt(), hour, binding.numberPickerMin.value)
                }
                // 데이터를 수정 했을 경우
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
                    setAlarm(this, requestCode.toInt(), hour, binding.numberPickerMin.value)
                }

                finish()
                // setting에 사용된 viewModel 변수들은 모두 초기화해야한다
            } else {
                makeToast(this, getString(R.string.alarmSet_Toast_week))
            }
        }

        // cancel 버튼 눌렀을 때
        binding.cancelButton.setOnSingleClickExt {
            finish()
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

    private fun initCurrentAlarmData(): Flow<List<AlarmData>> {
        return flowOf(
            listOf(
                AlarmData(
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
                    requestCode = "",
                    mode = 0,
                    hour = getCurrentHour(),
                    minute = getCurrentMinute(),
                    quick = false,
                    volume = 100,
                    bell = 0,
                    name = ""
                )
            )
        )
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
