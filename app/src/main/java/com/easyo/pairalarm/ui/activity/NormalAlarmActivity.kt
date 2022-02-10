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
import com.EasyO.pairalarm.ui.dialog.BellSelect
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.util.MakeAnimation
import com.easyo.pairalarm.util.makeToast
import com.easyo.pairalarm.util.setOnSingleClickExt
import com.easyo.pairalarm.viewModel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint

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

        var mon = false
        var tue = false
        var wed = false
        var thu = false
        var fri = false
        var sat = false
        var sun = false

        var vibration = 0

        // 수정하기 위해 이 Activity를 열었는지 확인
        val isModify = intent.getBooleanExtra("modify", false)

        // todo 수정하기 위해 들어왔다면 각 view에 데이터를 넣어줘야한다
        // todo 각각의 데이터는 AppClass에 들어있음
        if (isModify && alarmViewModel.currentAlarmData != null){

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

        // AlarmMode 설정 버튼 눌렀을 때
        binding.selectModeButton.setOnSingleClickExt {
            // ** 항목 선택 Dialog 설정
            val modeItem = arrayOf(getString(R.string.alarmSet_alarmModeItem1), getString(R.string.alarmSet_alarmModeItem2))
            val builder = AlertDialog.Builder(this)

            builder.setTitle(getString(R.string.alarmSet_selectBellDialogTitle))
            builder.setSingleChoiceItems(modeItem, alarmViewModel.currentAlarmMode.value, null)
            builder.setNeutralButton(getString(R.string.cancel), null)

            builder.setPositiveButton(getString(R.string.ok)){ dialogInterface: DialogInterface, i: Int ->
                val alert = dialogInterface as AlertDialog
                val idx = alert.listView.checkedItemPosition
                // * 선택된 아이템의 position에 따라 행동 조건 넣기
                when(idx){
                    // Normal 클릭 시
                    0 -> {
                        alarmViewModel.currentAlarmMode.value = 0
                        binding.textCurrentMode.text = getString(R.string.alarmSet_selectModeNormal)
                    }
                    // Calculate 클릭 시
                    1 -> {
                        alarmViewModel.currentAlarmMode.value = 1
                        binding.textCurrentMode.text = getString(R.string.alarmSet_selectModeCAL)
                    }
                }
            }
            builder.show()
        }

        // save 버튼 눌렀을 때
        binding.saveButton.setOnSingleClickExt {
            // 요일을 하나라도 선택해야한다
            if (sun || mon || tue || wed || thu || fri || sat){
                var hour = 0

                // 오전, 오후에 따라 hour의 값에 12를 더해주기
                if (binding.numberPickerAMPM.value == 0){
                    hour = binding.numberPickerHour.value
                }else{
                    hour = binding.numberPickerHour.value + 12
                    // 24시는 0시로 설정되게 한다
                    if (hour == 24){
                        hour = 0
                    }
                }

                // DB의 requestCode에 넣을 unique 수 생성
                val requestCode = System.currentTimeMillis()

                // DB에 넣을 Data set
                val alarmData = AlarmData(
                    // autoGenerate가 true이기 때문에 null을 넣으면 알아서 값이 들어간다
                    id = null,
                    button = true,
                    Sun = sun,
                    Mon = mon,
                    Tue = tue,
                    Wed = wed,
                    Thu = thu,
                    Fri = fri,
                    Sat = sat,
                    hour = hour,
                    minute = binding.numberPickerMin.value,
                    volume = binding.volumeSeekBar.progress,
                    // 지금은 임시로 bell을 0로 설정한다
                    bell = 0,
                    quick = false,
                    // 지금은 임시로 mode를 0로 설정한다
                    mode = alarmViewModel.currentAlarmMode.value,
                    vibration = vibration,
                    name = binding.alarmNameEditText.text.toString(),
                    requestCode = requestCode
                )

                // DB에 데이터 삽입
                alarmViewModel.insert(alarmData)

                // setting에 사용된 viewModel 변수들은 모두 초기화해야한다다
                alarmViewModel.currentAlarmData.value = null
                alarmViewModel.currentAlarmBell.value = null
                alarmViewModel.currentAlarmMode.value = 0

                finish()
            }else{
                makeToast(this, getString(R.string.alarmSet_Toast))
            }
        }

        // cancel 버튼 눌렀을 때
        binding.cancelButton.setOnSingleClickExt {
            finish()
        }

        // 진동 버튼을 눌렀을 때때
        binding.imageVibration.apply {
            setOnClickListener {
                when (vibration){
                    0 -> {
                        this.setImageDrawable(getDrawable(R.drawable.ic_vib_1))
                        vibration = 1
                    }
                    1 ->{
                        this.setImageDrawable(getDrawable(R.drawable.ic_vib_2))
                        vibration = 2
                        makeAnimation.swing(this).start()
                    }
                    2 ->{
                        this.setImageDrawable(getDrawable(R.drawable.ic_no_vib))
                        vibration = 0
                    }
                }
            }
        }

        // 볼륨 이미지를 클릭했을 때
        binding.imageVolume.setOnClickListener {
            if (binding.volumeSeekBar.progress > 0){
                binding.volumeSeekBar.progress = 0
                binding.imageVolume.setImageResource(R.drawable.volume_mute)
            }else{
                binding.volumeSeekBar.progress = 100
                binding.imageVolume.setImageResource(R.drawable.volume_icon)
            }
        }

        // 월
        binding.monButton.apply {
            setOnClickListener {
                if (!mon){
                    mon = true
                    setStrokeColorResource(R.color.deep_yellow)
                    // 점프 애니메이션
                    makeAnimation.jump(this).start()
                }else{
                    mon = false
                    setStrokeColorResource(R.color.background)
                    // 털어내는 애니메이션
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 화
        binding.tueButton.apply {
            setOnClickListener {
                if (!tue){
                    tue = true
                    setStrokeColorResource(R.color.deep_yellow)
                    makeAnimation.jump(this).start()
                }else{
                    tue = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 수
        binding.wedButton.apply {
            setOnClickListener {
                if (!wed){
                    wed = true
                    setStrokeColorResource(R.color.deep_yellow)
                    makeAnimation.jump(this).start()
                }else{
                    wed = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 목
        binding.thurButton.apply {
            setOnClickListener {
                if (!thu){
                    thu = true
                    setStrokeColorResource(R.color.deep_yellow)
                    makeAnimation.jump(this).start()
                }else{
                    thu = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 금
        binding.friButton.apply {
            setOnClickListener {
                if (!fri){
                    fri = true
                    setStrokeColorResource(R.color.deep_yellow)
                    makeAnimation.jump(this).start()
                }else{
                    fri = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 토
        binding.satButton.apply {
            setOnClickListener {
                if (!sat){
                    sat = true
                    setStrokeColorResource(R.color.main_deepBlue)
                    makeAnimation.jump(this).start()
                }else{
                    sat = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }

        // 일
        binding.sunButton.apply {
            setOnClickListener {
                if (!sun){
                    sun = true
                    setStrokeColorResource(R.color.red)
                    makeAnimation.jump(this).start()
                }else{
                    sun = false
                    setStrokeColorResource(R.color.background)
                    makeAnimation.swing(this).start()
                }
            }
        }
    }
}