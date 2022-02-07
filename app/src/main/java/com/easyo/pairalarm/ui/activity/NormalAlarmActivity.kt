package com.easyo.pairalarm.ui.activity

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityMakeAlarmBinding
import android.widget.EditText

import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.easyo.pairalarm.util.MakeAnimation
import com.easyo.pairalarm.util.setOnSingleClickExt


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

        var mon = false
        var tue = false
        var wed = false
        var thu = false
        var fri = false
        var sat = false
        var sun = false

        var vibration = 0

        val makeAnimation = MakeAnimation()
        // editText의 외부를 클릭했을 때는 키보드랑 Focus 제거하기
        binding.rootLayout.setOnClickListener {
            val imm =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

            binding.alarmNameEditTextLayout.clearFocus()
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
//                // 애니메이션 시작
//                animatorSet.start()
                if (!mon){
                    mon = true
                    setStrokeColorResource(R.color.deep_yellow)
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