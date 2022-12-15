package com.easyo.pairalarm.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
import timber.log.Timber

class BellSelectDialogFragment : DialogFragment() {
    private lateinit var binding: DialogBellSetBinding
    private val viewModel: AlarmViewModel by activityViewModels()
    private var bellIndex: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogBellSetBinding.inflate(inflater, container, false)
        bellIndex = arguments?.getInt("bellIndex") ?: 0
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // 배경 투명하게 만들기
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // 벨 인덱스 값에 따라 라디오 버튼 초기화 하기
        when (bellIndex) {
            0 -> binding.radioButtonN1.isChecked = true
            1 -> binding.radioButtonN2.isChecked = true
            2 -> binding.radioButtonN3.isChecked = true
            3 -> binding.radioButtonN4.isChecked = true
            else -> binding.radioButtonN1.isChecked = true
        }

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
                bellIndex?.let { AlarmMusic.setCurrentMusic(selectMusic(requireContext(), it)) }
            }

            // 음악 재생중일 때 -> 음악 정지
            if (AlarmMusic.getCurrentMusic()!!.isPlaying) {
                mediaStop()
                binding.playButton.text = requireContext().getString(R.string.play)
                Timber.d("playing stop")
            }
            // 음악 재생중 아닐 때 -> 음악 시작
            else {
                bellIndex?.let { AlarmMusic.setCurrentMusic(selectMusic(requireContext(), it)) }
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
            bellIndex?.let {
                AlarmBell.setBellIndex(it)
                viewModel.currentAlarmBell.value = it
                dismiss()
            }
        }
    }

    private fun mediaStop() {
        // 무언가 미디어에서 재생중일 때
        AlarmMusic.getCurrentMusic()?.let {
            // 음악 재생중
            if (it.isPlaying) {
                it.apply {
                    stop()
                    release()
                }
            } else {
                it.apply {
                    release()
                }
            }
            AlarmMusic.setCurrentMusic(null)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaStop()
    }
}
