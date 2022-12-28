package com.easyo.pairalarm.groupieitem

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.content.res.AppCompatResources
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.databinding.AlarmItemBinding
import com.easyo.pairalarm.extensions.lastClickTime
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.ui.activity.NormalAlarmActivity
import com.easyo.pairalarm.util.ALARM_CODE_TEXT
import com.easyo.pairalarm.util.SimpleDialog
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.xwray.groupie.databinding.BindableItem
import timber.log.Timber

class AlarmGroupie(
    val context: Context,
    val alarmData: AlarmData,
    private val alarmViewModel: AlarmViewModel
) : BindableItem<AlarmItemBinding>(alarmData.hashCode().toLong()) {

    @SuppressLint("SetTextI18n")
    override fun bind(binding: AlarmItemBinding, position: Int) {
        binding.alarmNameText.text = alarmData.name
        if (alarmData.name.isNotEmpty()) {
            binding.alarmNameText.isSelected = true
        }

        // 시간 표기
        binding.hourText.text = alarmData.hour.toString()

        // 두 자릿수로 표현 - 분
        if (alarmData.minute < 10) {
            binding.minText.text = "0" + alarmData.minute.toString()
        } else {
            binding.minText.text = alarmData.minute.toString()
        }

        // 요일 표기
        if (alarmData.Mon) {
            binding.monText.setTextColor(Color.YELLOW)
        } else {
            binding.monText.setTextColor(context.getColor(R.color.new_subTextColor))
        }

        if (alarmData.Tue) {
            binding.tueText.setTextColor(Color.YELLOW)
        } else {
            binding.tueText.setTextColor(context.getColor(R.color.new_subTextColor))
        }

        if (alarmData.Wed) {
            binding.wedText.setTextColor(Color.YELLOW)
        } else {
            binding.wedText.setTextColor(context.getColor(R.color.new_subTextColor))
        }

        if (alarmData.Thu) {
            binding.thuText.setTextColor(Color.YELLOW)
        } else {
            binding.thuText.setTextColor(context.getColor(R.color.new_subTextColor))
        }

        if (alarmData.Fri) {
            binding.friText.setTextColor(Color.YELLOW)
        } else {
            binding.friText.setTextColor(context.getColor(R.color.new_subTextColor))
        }

        if (alarmData.Sat) {
            binding.satText.setTextColor(context.getColor(R.color.light_blue))
        } else {
            binding.satText.setTextColor(context.getColor(R.color.new_subTextColor))
        }

        if (alarmData.Sun) {
            binding.sunText.setTextColor(Color.RED)
        } else {
            binding.sunText.setTextColor(context.getColor(R.color.new_subTextColor))
        }

        // 진동 표기
        when (alarmData.vibration) {
            0 -> binding.vibImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_no_vib
                )
            )
            1 -> binding.vibImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_vib_1
                )
            )
            2 -> binding.vibImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.ic_vib_2
                )
            )
        }

        // 볼륨 표기
        when (alarmData.volume) {
            100 -> binding.volumeImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.vol4
                )
            )
            in 61..99 -> binding.volumeImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.vol3
                )
            )
            in 31..60 -> binding.volumeImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.vol2
                )
            )
            in 1..30 -> binding.volumeImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.vol1
                )
            )
            else -> binding.volumeImageView.setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.vol0
                )
            )
        }

        binding.root.setOnSingleClickListener {
            openNormalAlarmActivity()
        }

        // 삭제 버튼 클릭
        binding.deleteImage.setOnSingleClickListener {
            SimpleDialog.make(
                context,
                context.getString(R.string.dialog_delete_title),
                context.getString(R.string.dialog_delete_content),
                null,
                positive = {
                    alarmViewModel.deleteAlarmData(this.context, alarmData)
                },
                negative = { }
            )
        }

        // on/off
        binding.onOffSwitch.isChecked = alarmData.button

        binding.onOffSwitch.setOnCheckedChangeListener { _, isChecked ->
            // 한 번만 클릭되는 기능을 넣지 않으면 혼자서 여러번 클릭됨
            if (lastClickTime < System.currentTimeMillis() - 500) {
                lastClickTime = System.currentTimeMillis()
                alarmData.button = isChecked
                alarmViewModel.updateAlarmData(context, alarmData)
                Timber.d("update alarmData: $alarmData")
            }
        }
    }

    private fun openNormalAlarmActivity() {
        val intent = Intent(context, NormalAlarmActivity::class.java)
        intent.putExtra(ALARM_CODE_TEXT, alarmData.alarmCode)
        context.startActivity(intent)
    }

    override fun getLayout(): Int {
        return R.layout.alarm_item
    }
}
