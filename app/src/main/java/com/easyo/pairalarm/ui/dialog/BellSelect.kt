package com.easyo.pairalarm.ui.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.DialogBellSetBinding
import com.easyo.pairalarm.util.selectMusic
import com.easyo.pairalarm.util.setOnSingleClickExt
import com.easyo.pairalarm.viewModel.AlarmViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BellSelect(context: Context, val alarmViewModel: AlarmViewModel):Dialog(context) {
    private lateinit var binding: DialogBellSetBinding
    private lateinit var uiJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBellSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bellIndex = 0

        // UI 값 세팅하기
        uiJob = CoroutineScope(Dispatchers.Main).launch {
            launch {
                alarmViewModel.playStopTextView.collectLatest {
                    binding.playButton.text = it
                }
            }
            launch {
                alarmViewModel.currentAlarmBell.collectLatest {
                    when(it){
                        0 -> binding.RadioGroup.check(R.id.radioButton_N1)
                        1 -> binding.RadioGroup.check(R.id.radioButton_N2)
                        2 -> binding.RadioGroup.check(R.id.radioButton_N3)
                        3 -> binding.RadioGroup.check(R.id.radioButton_N4)
                    }

                }
            }
        }

        // 배경 투명하게 만들기
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 라디오 버튼 클릭에 따라 인덱스 값 지정하기
        binding.RadioGroup.setOnCheckedChangeListener { radioGroup, checkedId ->
            when(checkedId){
                R.id.radioButton_N1 -> bellIndex = 0
                R.id.radioButton_N2 -> bellIndex = 1
                R.id.radioButton_N3 -> bellIndex = 2
                R.id.radioButton_N4 -> bellIndex = 3
            }
            mediaStop(false)
        }

        // Play 버튼
        binding.playButton.setOnSingleClickExt {
            if (AppClass.mediaPlayer == null){
                AppClass.mediaPlayer = selectMusic(context, bellIndex)
            }

            // 음악 재생중일 때 -> 음악 정지
            if (AppClass.mediaPlayer!!.isPlaying){
                mediaStop(false)
                alarmViewModel.playStopTextView.value = context.getString(R.string.play)
                Log.d("BellSelect", "playing stop")
            }
            // 음악 재생중 아닐 때 -> 음악 시작
            else{
                AppClass.mediaPlayer = selectMusic(context, bellIndex)
                AppClass.mediaPlayer!!.run {
                    setVolume(1f, 1f)
                    isLooping = true
                    start()
                }
                alarmViewModel.playStopTextView.value = context.getString(R.string.stop)
            }
        }

        // Save 버튼
        binding.saveButton.setOnSingleClickExt {
            alarmViewModel.currentAlarmBell.value = bellIndex
            dismiss()
        }
    }

    override fun onStop() {
        super.onStop()
        mediaStop(true)
        uiJob.cancel()
    }
    private fun mediaStop(dialogClose: Boolean){
        if (AppClass.mediaPlayer != null){
            // 음악 재생중 + Dialog를 완전히 닫았을 때
            if(AppClass.mediaPlayer!!.isPlaying || dialogClose){
                AppClass.mediaPlayer?.stop()
                AppClass.mediaPlayer?.release()
                AppClass.mediaPlayer = null
            }
            // 음악만 정지하고 싶을 때
            else if(AppClass.mediaPlayer!!.isPlaying || !dialogClose){
                AppClass.mediaPlayer!!.stop()
            }
            // playing이 아닌 상태 + dialog를 닫고 싶을 때
            else if(!AppClass.mediaPlayer!!.isPlaying || dialogClose){
                AppClass.mediaPlayer?.release()
                AppClass.mediaPlayer = null
            }
        }
    }
}