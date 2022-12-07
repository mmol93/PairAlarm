package com.easyo.pairalarm.ui.activity

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.databinding.ActivityMakeAlarmBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.ui.dialog.BellSelectDialogFragment
import com.easyo.pairalarm.util.*
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class NormalAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMakeAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()
    private lateinit var currentAlarmFlowData: Flow<AlarmData>
    private lateinit var currentAlarmData: AlarmData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMakeAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // TODO: 피커들 BindingAdapter에서 초기화 하게 바꾸기
        // numberPicker의 시간 부분 최대, 최소값 설정
        binding.numberPickerHour.maxValue = 12
        binding.numberPickerHour.minValue = 1

        // numberPicker의 분 부분 최대, 최소값 설정
        binding.numberPickerMin.maxValue = 59
        binding.numberPickerMin.minValue = 0

        // numberPicker에 오전 오후 텍스트 세팅
        val arg1 = arrayOf(getString(R.string.alarmSet_AM), getString(R.string.alarmSet_PM))
        binding.numberPickerAMPM.minValue = 0
        binding.numberPickerAMPM.maxValue = arg1.size - 1
        binding.numberPickerAMPM.displayedValues = arg1

        val bellSelectDialog = BellSelectDialogFragment()

        var alarmCode = intent.getStringExtra("alarmCode")
        currentAlarmFlowData = if (alarmCode != null) {
            alarmViewModel.searchAlarmCode(alarmCode)
        } else {
            initCurrentAlarmData()
        }

        // alarmData의 alarmCode 값이 0이면 새로운 알람 생성
        if (alarmCode == null) {
            binding.saveButton.text = getString(R.string.save)
        }

        // bellDialog에서 변경한 bellIndex를 받아온다
        lifecycleScope.launch {
            alarmViewModel.currentAlarmBell.collectLatest { bellIndex ->
                Timber.d("bellIndex: $bellIndex")
                setAlarmBellText(AlarmBell.getBellIndex())
                if (::currentAlarmData.isInitialized) {
                    currentAlarmData.bell = AlarmBell.getBellIndex()
                }
            }
        }

        lifecycleScope.launch {
            currentAlarmFlowData.collectLatest { alarmData ->
                currentAlarmData = alarmData
                binding.alarmData = alarmData
                Timber.d("alarmData: $alarmData")

                binding.alarmNameEditText.setText(alarmData.name)

                binding.volumeSeekBar.progress = alarmData.volume

                setAlarmBellText(alarmData.bell)
                AlarmBell.setBellIndex(alarmData.bell)

                setAlarmModeText(alarmData.mode)

                binding.monButton.setStrokeColorInButton(null, alarmData.Mon)
                binding.tueButton.setStrokeColorInButton(null, alarmData.Tue)
                binding.wedButton.setStrokeColorInButton(null, alarmData.Wed)
                binding.thurButton.setStrokeColorInButton(null, alarmData.Thu)
                binding.friButton.setStrokeColorInButton(null, alarmData.Fri)
                binding.satButton.setStrokeColorInButton("Sat", alarmData.Sat)
                binding.sunButton.setStrokeColorInButton("Sun", alarmData.Sun)
            }
        }

        // editText의 외부를 클릭했을 때는 키보드랑 Focus 제거하기
        binding.rootLayout.setOnClickListener {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            binding.alarmNameEditTextLayout.clearFocus()
        }

        // AlarmBell 설정 버튼 눌렀을 때
        binding.selectBellButton.setOnSingleClickListener {
            bellSelectDialog.show(supportFragmentManager, null)
        }

        // AlarmMode 설정 버튼 눌렀을 때
        binding.selectModeButton.setOnSingleClickListener {
            // TODo: 이 부분 전체 공통화 작업 해놓기
            // TODO: 이 부분 enum이나 data class로 변경해서 사용하기(SimpleAlarm 부분도 동일하게 적용)
            // ** 항목 선택 Dialog 설정
            val modeItems = arrayOf(
                getString(R.string.alarmSet_alarmModeItem1),
                getString(R.string.alarmSet_alarmModeItem2)
            )
            val builder = AlertDialog.Builder(this)

            builder.setTitle(getString(R.string.alarmSet_selectBellDialogTitle))
            builder.setSingleChoiceItems(
                modeItems,
                currentAlarmData.mode,
                null
            )
            builder.setNeutralButton(getString(R.string.cancel), null)

            builder.setPositiveButton(getString(R.string.ok)) { dialogInterface: DialogInterface, _: Int ->
                val alert = dialogInterface as AlertDialog
                when (alert.listView.checkedItemPosition) {
                    // Normal 클릭 시
                    0 -> {
                        currentAlarmData.mode = 0
                    }
                    // Calculate 클릭 시
                    1 -> {
                        currentAlarmData.mode = 1
                    }
                }
                setAlarmModeText(currentAlarmData.mode)
            }
            builder.show()
        }

        // save 버튼 눌렀을 때
        binding.saveButton.setOnSingleClickListener {
            // 요일을 하나라도 선택해야한다
            if (currentAlarmData.Mon ||
                currentAlarmData.Tue ||
                currentAlarmData.Wed ||
                currentAlarmData.Thu ||
                currentAlarmData.Fri ||
                currentAlarmData.Sat ||
                currentAlarmData.Sun
            ) {
                // 오전, 오후에 따라 hour의 값에 12를 더해주기
                val hour =
                    if (binding.numberPickerHour.value == 12 && binding.numberPickerAMPM.value == 1) {
                        12
                    } else if (binding.numberPickerHour.value == 12 && binding.numberPickerAMPM.value == 0) {
                        0
                    } else if (binding.numberPickerAMPM.value == 1 && binding.numberPickerHour.value + 12 <= 24) {
                        binding.numberPickerHour.value + 12
                    } else {
                        binding.numberPickerHour.value
                    }

                // currentAlarmCode를 보고 새로운 알람 생성인지 수정인지 판단
                if (alarmCode == null) {
                    alarmCode = getNewAlarmCode()
                    // DB에 넣을 Data set
                    val alarmData = AlarmData(
                        // autoGenerate가 true이기 때문에 null을 넣으면 알아서 값이 들어간다
                        id = null,
                        button = true,
                        Sun = currentAlarmData.Sun,
                        Mon = currentAlarmData.Mon,
                        Tue = currentAlarmData.Tue,
                        Wed = currentAlarmData.Wed,
                        Thu = currentAlarmData.Thu,
                        Fri = currentAlarmData.Fri,
                        Sat = currentAlarmData.Sat,
                        hour = hour,
                        minute = binding.numberPickerMin.value,
                        volume = binding.volumeSeekBar.progress,
                        bell = currentAlarmData.bell,
                        quick = false,
                        mode = currentAlarmData.mode,
                        vibration = currentAlarmData.vibration,
                        name = binding.alarmNameEditText.text.toString(),
                        alarmCode = alarmCode!!
                    )

                    // DB에 데이터 삽입
                    alarmViewModel.insertAlarmData(alarmData)

                    // 브로드캐스트에 알람 예약하기
                    setAlarm(this, alarmCode!!.toInt(), hour, binding.numberPickerMin.value)
                }
                // 데이터를 수정 했을 경우
                else {
                    val alarmData = AlarmData(
                        // autoGenerate가 true이기 때문에 null을 넣으면 알아서 값이 들어간다
                        id = currentAlarmData.id,
                        button = true,
                        Sun = currentAlarmData.Sun,
                        Mon = currentAlarmData.Mon,
                        Tue = currentAlarmData.Tue,
                        Wed = currentAlarmData.Wed,
                        Thu = currentAlarmData.Thu,
                        Fri = currentAlarmData.Fri,
                        Sat = currentAlarmData.Sat,
                        hour = hour,
                        minute = binding.numberPickerMin.value,
                        volume = binding.volumeSeekBar.progress,
                        bell = currentAlarmData.bell,
                        quick = false,
                        mode = currentAlarmData.mode,
                        vibration = currentAlarmData.vibration,
                        name = binding.alarmNameEditText.text.toString(),
                        alarmCode = alarmCode!!
                    )

                    // DB 업데이트
                    alarmViewModel.updateAlarData(alarmData)
                    setAlarm(this, alarmCode!!.toInt(), hour, binding.numberPickerMin.value)
                }

                finish()
            } else {
                makeToast(this, getString(R.string.alarmSet_Toast_week))
            }
        }

        // cancel 버튼 눌렀을 때
        binding.cancelButton.setOnSingleClickListener {
            finish()
        }

        // 진동 버튼을 눌렀을 때때
        binding.imageVibration.apply {
            setOnClickListener {
                when (currentAlarmData.vibration) {
                    0 -> {
                        currentAlarmData.vibration = 1
                        binding.imageVibration.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this@NormalAlarmActivity,
                                R.drawable.ic_vib_1
                            )
                        )
                    }
                    1 -> {
                        currentAlarmData.vibration = 2
                        binding.imageVibration.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this@NormalAlarmActivity,
                                R.drawable.ic_vib_2
                            )
                        )
                        AlarmAnimation.swing(this).start()
                    }
                    2 -> {
                        binding.imageVibration.setImageDrawable(
                            AppCompatResources.getDrawable(
                                this@NormalAlarmActivity,
                                R.drawable.ic_no_vib
                            )
                        )
                        currentAlarmData.vibration = 0
                    }
                }
            }
        }

        // 볼륨 이미지를 클릭했을 때
        binding.imageVolume.setOnClickListener {
            if (currentAlarmData.volume > 0) {
                currentAlarmData.volume = 0
                binding.imageVolume.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@NormalAlarmActivity,
                        R.drawable.volume_mute
                    )
                )
                binding.volumeSeekBar.progress = 0
            } else {
                currentAlarmData.volume = 100
                binding.imageVolume.setImageDrawable(
                    AppCompatResources.getDrawable(
                        this@NormalAlarmActivity,
                        R.drawable.volume_icon
                    )
                )
                binding.volumeSeekBar.progress = 100
            }
        }
        binding.volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, p1: Int, p2: Boolean) {

            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                if (seekBar?.progress == 0) {
                    binding.imageVolume.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@NormalAlarmActivity,
                            R.drawable.volume_mute
                        )
                    )
                } else {
                    binding.imageVolume.setImageDrawable(
                        AppCompatResources.getDrawable(
                            this@NormalAlarmActivity,
                            R.drawable.volume_icon
                        )
                    )
                }
            }
        })

        // 월
        binding.monButton.apply {
            setOnClickListener {
                if (!currentAlarmData.Mon) {
                    currentAlarmData.Mon = true
                    // 점프 애니메이션
                    AlarmAnimation.jump(this).start()
                } else {
                    currentAlarmData.Mon = false
                    // 털어내는 애니메이션
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, currentAlarmData.Mon)
            }
        }

        // 화
        binding.tueButton.apply {
            setOnClickListener {
                if (!currentAlarmData.Tue) {
                    currentAlarmData.Tue = true
                    AlarmAnimation.jump(this).start()
                } else {
                    currentAlarmData.Tue = false
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, currentAlarmData.Tue)
            }
        }

        // 수
        binding.wedButton.apply {
            setOnClickListener {
                if (!currentAlarmData.Wed) {
                    currentAlarmData.Wed = true
                    AlarmAnimation.jump(this).start()
                } else {
                    currentAlarmData.Wed = false
                    setStrokeColorResource(R.color.background)
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, currentAlarmData.Wed)
            }
        }

        // 목
        binding.thurButton.apply {
            setOnClickListener {
                if (!currentAlarmData.Thu) {
                    currentAlarmData.Thu = true
                    AlarmAnimation.jump(this).start()
                } else {
                    currentAlarmData.Thu = false
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, currentAlarmData.Thu)
            }
        }

        // 금
        binding.friButton.apply {
            setOnClickListener {
                if (!currentAlarmData.Fri) {
                    currentAlarmData.Fri = true
                    AlarmAnimation.jump(this).start()
                } else {
                    currentAlarmData.Fri = false
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton(null, currentAlarmData.Fri)
            }
        }

        // 토
        binding.satButton.apply {
            setOnClickListener {
                if (!currentAlarmData.Sat) {
                    currentAlarmData.Sat = true
                    AlarmAnimation.jump(this).start()
                } else {
                    currentAlarmData.Sat = false
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton("Sat", currentAlarmData.Sat)
            }
        }

        // 일
        binding.sunButton.apply {
            setOnClickListener {
                if (!currentAlarmData.Sun) {
                    currentAlarmData.Sun = true
                    AlarmAnimation.jump(this).start()
                } else {
                    currentAlarmData.Sun = false
                    setStrokeColorResource(R.color.background)
                    AlarmAnimation.swing(this).start()
                }
                setStrokeColorInButton("Sun", currentAlarmData.Sun)
            }
        }
    }

    private fun initCurrentAlarmData(): Flow<AlarmData> {
        return flowOf(
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
                alarmCode = "",
                mode = 0,
                hour = getCurrentHour(),
                minute = getCurrentMinute(),
                quick = false,
                volume = 100,
                bell = 0,
                name = ""
            )
        )
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

    private fun setAlarmBellText(bellIndex: Int) {
        Timber.d("current bell index: $bellIndex")
        when (bellIndex) {
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

    private fun setAlarmModeText(modeIndex: Int) {
        when (modeIndex) {
            0 -> binding.textCurrentMode.text =
                getString(R.string.alarmSet_alarmModeItem1)
            1 -> binding.textCurrentMode.text =
                getString(R.string.alarmSet_alarmModeItem2)
        }
    }
}
