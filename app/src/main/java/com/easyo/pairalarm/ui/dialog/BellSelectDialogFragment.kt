package com.easyo.pairalarm.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.DialogBellSetBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.util.AlarmBell
import com.easyo.pairalarm.util.AlarmMusic
import com.easyo.pairalarm.util.selectMusic
import com.easyo.pairalarm.viewModel.AlarmViewModel

class BellSelectDialogFragment : DialogFragment() {
    private lateinit var binding: DialogBellSetBinding
    private val viewModel: AlarmViewModel by activityViewModels()
    private var bellIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBellSetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 배경 투명하게 만들기
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 라디오 버튼 클릭에 따라 인덱스 값 지정하기
        binding.RadioGroup.setOnCheckedChangeListener { _, checkedId ->
            mediaStop()
            when (checkedId) {
                R.id.radioButton_N1 -> bellIndex = 0
                R.id.radioButton_N2 -> bellIndex = 1
                R.id.radioButton_N3 -> bellIndex = 2
                R.id.radioButton_N4 -> bellIndex = 3
            }
        }

        // Play 버튼
        binding.playButton.setOnSingleClickListener {
            if (AlarmMusic.getCurrentMusic() == null) {
                AlarmMusic.setCurrentMusic(selectMusic(requireContext(), bellIndex))
            }

            // 음악 재생중일 때 -> 음악 정지
            if (AlarmMusic.getCurrentMusic()!!.isPlaying) {
                mediaStop()
                binding.playButton.text = requireContext().getString(R.string.play)

                Log.d("BellSelect", "playing stop")
            }
            // 음악 재생중 아닐 때 -> 음악 시작
            else {
                AlarmMusic.setCurrentMusic(selectMusic(requireContext(), bellIndex))
                AlarmMusic.getCurrentMusic()!!.run {
                    setVolume(1f, 1f)
                    isLooping = true
                    start()
                }
                binding.playButton.text = requireContext().getString(R.string.stop)
            }
        }

        // Save 버튼
        binding.saveButton.setOnSingleClickListener {
            AlarmBell.setBellIndex(bellIndex)
            viewModel.currentAlarmBell.value = bellIndex
            dismiss()
        }
    }

    private fun mediaStop() {
        // 무언가 미디어에서 재생중일 때
        AlarmMusic.getCurrentMusic()?.let {
            // 음악 재생중
            if (it.isPlaying){
                it.apply {
                    stop()
                    release()
                }
            }else{
                it.apply {
                    release()
                }
            }
            AlarmMusic.setCurrentMusic(null)
        }
    }
}
