package com.easyo.pairalarm.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.DialogBellSetBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.util.AlarmBell
import com.easyo.pairalarm.util.AlarmMusic
import com.easyo.pairalarm.util.selectMusic

class BellSelect(
    context: Context
) : Dialog(context) {
    private lateinit var binding: DialogBellSetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogBellSetBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var bellIndex = 0

        if (AlarmBell.getBellIndex() != 0) {
            bellIndex = AlarmBell.getBellIndex()
        }

        // 배경 투명하게 만들기
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 라디오 버튼 클릭에 따라 인덱스 값 지정하기
        binding.RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            mediaStop(false)
            when(checkedId){
                R.id.radioButton_N1 -> bellIndex = 0
                R.id.radioButton_N2 -> bellIndex = 1
                R.id.radioButton_N3 -> bellIndex = 2
                R.id.radioButton_N4 -> bellIndex = 3
            }
        }

        // Play 버튼
        binding.playButton.setOnSingleClickListener {
            if (AlarmMusic.getCurrentMusic() == null) {
                AlarmMusic.setCurrentMusic(selectMusic(context, bellIndex))
            }

            // 음악 재생중일 때 -> 음악 정지
            if (AlarmMusic.getCurrentMusic()!!.isPlaying) {
                mediaStop(false)
                binding.playButton.text = context.getString(R.string.play)

                Log.d("BellSelect", "playing stop")
            }
            // 음악 재생중 아닐 때 -> 음악 시작
            else {
                AlarmMusic.setCurrentMusic(selectMusic(context, bellIndex))
                AlarmMusic.getCurrentMusic()!!.run {
                    setVolume(1f, 1f)
                    isLooping = true
                    start()
                }
                binding.playButton.text = context.getString(R.string.stop)
            }
        }

        // Save 버튼
        binding.saveButton.setOnSingleClickListener {
            AlarmBell.setBellIndex(bellIndex)
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        when (AlarmBell.getBellIndex()) {
            0 -> binding.RadioGroup.check(R.id.radioButton_N1)
            1 -> binding.RadioGroup.check(R.id.radioButton_N2)
            2 -> binding.RadioGroup.check(R.id.radioButton_N3)
            3 -> binding.RadioGroup.check(R.id.radioButton_N4)
        }
    }

    override fun onStop() {
        super.onStop()
        mediaStop(true)
    }

    private fun mediaStop(dialogClose: Boolean) {
        if (AlarmMusic.getCurrentMusic() != null) {
            // 음악 재생중 + Dialog를 완전히 닫았을 때
            if (AlarmMusic.getCurrentMusic()!!.isPlaying || dialogClose) {
                AlarmMusic.getCurrentMusic()?.stop()
                AlarmMusic.getCurrentMusic()?.release()
                AlarmMusic.setCurrentMusic(null)
            }
            // 음악만 정지하고 싶을 때
            else if (AlarmMusic.getCurrentMusic()!!.isPlaying || !dialogClose) {
                AlarmMusic.getCurrentMusic()!!.stop()
            }
            // playing이 아닌 상태 + dialog를 닫고 싶을 때
            else if (!AlarmMusic.getCurrentMusic()!!.isPlaying || dialogClose) {
                AlarmMusic.getCurrentMusic()?.release()
                AlarmMusic.setCurrentMusic(null)
            }
        }
    }
}
