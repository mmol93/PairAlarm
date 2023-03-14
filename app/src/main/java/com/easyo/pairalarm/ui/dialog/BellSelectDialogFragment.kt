package com.easyo.pairalarm.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.DialogBellSetBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.util.AlarmMusic
import com.easyo.pairalarm.util.selectMusic
import timber.log.Timber

class BellSelectDialogFragment(
    private val selectedBellIndex: Int,
    private val clickSaveButton: (clickedBellIndex: Int) -> Unit
) : DialogFragment() {
    private lateinit var binding: DialogBellSetBinding
    private var bellIndex = selectedBellIndex

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

        // 라디오 버튼 초기화
        when (selectedBellIndex) {
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
                AlarmMusic.setCurrentMusic(selectMusic(requireContext(), bellIndex))
            }
            AlarmMusic.getCurrentMusic()?.let { currentMusic ->
                // 음악 재생중일 때 -> 음악 정지
                if (currentMusic.isPlaying) {
                    mediaStop()
                    binding.playButton.text = requireContext().getString(R.string.play)
                    Timber.d("playing stop")
                }
                // 음악 재생중 아닐 때 -> 음악 시작
                else {
                    AlarmMusic.setCurrentMusic(selectMusic(requireContext(), bellIndex))
                    currentMusic.run {
                        setVolume(1f, 1f)
                        isLooping = true
                        start()
                    }
                    binding.playButton.text = requireContext().getString(R.string.stop)
                }
            }
        }

        // Save 버튼
        binding.saveButton.setOnSingleClickListener {
            clickSaveButton.invoke(bellIndex)
            dismiss()
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
