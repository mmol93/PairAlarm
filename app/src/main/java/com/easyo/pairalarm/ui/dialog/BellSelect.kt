package com.EasyO.pairalarm.ui.dialog

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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BellSelect(context: Context, val alarmViewModel: AlarmViewModel):Dialog(context) {
    private lateinit var binding: DialogBellSetBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBellSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bellIndex = 0

        // UI 값 세팅하기
        binding.playButton.text = alarmViewModel.playStopTextView.value
        CoroutineScope(Dispatchers.IO).launch {
            alarmViewModel.playStopTextView.collectLatest {
                launch(Dispatchers.Main) {
                    binding.playButton.text = it
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
                AppClass.mediaPlayer!!.apply {
                    setVolume(1f, 1f)
                    isLooping = true
                    start()
                }
                alarmViewModel.playStopTextView.value = context.getString(R.string.stop)
            }
            Log.d("BellSelect", "---------------")
            Log.d("BellSelect", "isPlaying: ${AppClass.mediaPlayer!!.isPlaying}")

        }

        binding.saveButton.setOnSingleClickExt {
            alarmViewModel.currentAlarmMode.value = bellIndex
        }
    }

    override fun onStop() {
        super.onStop()
        mediaStop(true)
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