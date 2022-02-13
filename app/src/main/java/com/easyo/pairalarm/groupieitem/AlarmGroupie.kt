package com.easyo.pairalarm.groupieitem

import android.annotation.SuppressLint
import android.content.Context
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.databinding.AlarmItemBinding
import com.xwray.groupie.databinding.BindableItem

class AlarmGroupie(val context: Context, val alarmDataList: AlarmData): BindableItem<AlarmItemBinding>() {
    @SuppressLint("SetTextI18n")
    override fun bind(binding: AlarmItemBinding, position: Int) {
        binding.alarmNameText.text = alarmDataList.name
        if (alarmDataList.name.length > 0){
            binding.alarmNameText.isSelected = true
        }

        // 시간 표기
        if (alarmDataList.hour > 12){
            binding.hourText.text = (alarmDataList.hour - 12).toString()
            binding.ampmText.text = context.getString(R.string.alarmSet_PM)
        }else{
            // 두 자릿수로 표현 - 시간
            if (alarmDataList.hour < 10){
                binding.hourText.text = "0" + alarmDataList.hour.toString()
            }else{
                binding.hourText.text = alarmDataList.hour.toString()
            }
            binding.ampmText.text = context.getString(R.string.alarmSet_AM)
        }

        // 두 자릿수로 표현 - 분
        if (alarmDataList.minute < 10){
            binding.minText.text = "0" + alarmDataList.minute.toString()
        }else{
            binding.minText.text = alarmDataList.minute.toString()
        }

        // 요일 표기
        if (alarmDataList.Mon){
            binding.monText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmDataList.Tue){
            binding.tueText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmDataList.Wed){
            binding.wedText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmDataList.Thu){
            binding.thuText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmDataList.Fri){
            binding.friText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmDataList.Sat){
            binding.satText.setTextColor(context.getColor(R.color.main_blue))
        }
        if (alarmDataList.Sun){
            binding.sunText.setTextColor(context.getColor(R.color.red))
        }

        // 진동 표기
        when(alarmDataList.vibration){
            0 -> binding.vibImageView.setImageDrawable(context.getDrawable(R.drawable.ic_no_vib))
            1 -> binding.vibImageView.setImageDrawable(context.getDrawable(R.drawable.ic_vib_1))
            2 -> binding.vibImageView.setImageDrawable(context.getDrawable(R.drawable.ic_vib_2))
        }

        // 볼륨 표기
        if (alarmDataList.volume == 100) binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol4))
        else if (alarmDataList.volume in 61..99) binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol3))
        else if (alarmDataList.volume in 31..60) binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol2))
        else if (alarmDataList.volume in 1..30) binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol1))
        else binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol0))

        // 스위치
        binding.onOffSwitch.isChecked = alarmDataList.button
    }

    override fun getLayout(): Int {
        return R.layout.alarm_item
    }
}