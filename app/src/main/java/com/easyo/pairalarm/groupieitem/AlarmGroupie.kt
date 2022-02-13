package com.easyo.pairalarm.groupieitem

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.databinding.AlarmItemBinding
import com.easyo.pairalarm.ui.activity.NormalAlarmActivity
import com.easyo.pairalarm.util.setOnSingleClickExt
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.xwray.groupie.databinding.BindableItem

class AlarmGroupie(val context: Context, val alarmData: AlarmData, private val alarmViewModel: AlarmViewModel): BindableItem<AlarmItemBinding>() {
    @SuppressLint("SetTextI18n")
    override fun bind(binding: AlarmItemBinding, position: Int) {
        binding.alarmNameText.text = alarmData.name
        if (alarmData.name.length > 0){
            binding.alarmNameText.isSelected = true
        }

        // 시간 표기
        if (alarmData.hour > 12){
            binding.hourText.text = (alarmData.hour - 12).toString()
            binding.ampmText.text = context.getString(R.string.alarmSet_PM)
        }else{
            // 시간은 두 자릿수로 표현하지 않는다
            binding.hourText.text = alarmData.hour.toString()
            binding.ampmText.text = context.getString(R.string.alarmSet_AM)
        }

        // 두 자릿수로 표현 - 분
        if (alarmData.minute < 10){
            binding.minText.text = "0" + alarmData.minute.toString()
        }else{
            binding.minText.text = alarmData.minute.toString()
        }

        // 요일 표기
        if (alarmData.Mon){
            binding.monText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmData.Tue){
            binding.tueText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmData.Wed){
            binding.wedText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmData.Thu){
            binding.thuText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmData.Fri){
            binding.friText.setTextColor(context.getColor(R.color.deep_yellow))
        }
        if (alarmData.Sat){
            Log.d("AlarmGroupie", "Blue")
            binding.satText.setTextColor(context.getColor(R.color.main_blue))
        }
        if (alarmData.Sun){
            Log.d("AlarmGroupie", "Red")
            binding.sunText.setTextColor(context.getColor(R.color.red))
        }

        // 진동 표기
        when(alarmData.vibration){
            0 -> binding.vibImageView.setImageDrawable(context.getDrawable(R.drawable.ic_no_vib))
            1 -> binding.vibImageView.setImageDrawable(context.getDrawable(R.drawable.ic_vib_1))
            2 -> binding.vibImageView.setImageDrawable(context.getDrawable(R.drawable.ic_vib_2))
        }

        // 볼륨 표기
        if (alarmData.volume == 100) binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol4))
        else if (alarmData.volume in 61..99) binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol3))
        else if (alarmData.volume in 31..60) binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol2))
        else if (alarmData.volume in 1..30) binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol1))
        else binding.volumeImageView.setImageDrawable(context.getDrawable(R.drawable.vol0))

        // 스위치
        binding.onOffSwitch.isChecked = alarmData.button

        binding.timeContainer.setOnSingleClickExt {
            openNormalAlarmActivity()
        }

        binding.alarmNameText.setOnSingleClickExt {
            openNormalAlarmActivity()
        }
    }

    private fun openNormalAlarmActivity(){
        Log.d("AlarmGroupie", "clicked Data: $alarmData")
        // AppClass.alarmViewModel: NormalAlarmActivity에서 사용하기 위해 AlarmFragment에서 사용중인 viewModel를 공유해준다
        // Activity끼리는 viewModel를 공유할 수 없기 때문에 이러한 방법을 사용함
        AppClass.alarmViewModel = alarmViewModel

        AppClass.alarmViewModel.currentAlarmData.value = alarmData
        AppClass.alarmViewModel.currentAlarmId.value = alarmViewModel.currentAlarmData.value.id
        AppClass.alarmViewModel.currentAlarmMon.value = alarmViewModel.currentAlarmData.value.Mon
        AppClass.alarmViewModel.currentAlarmTue.value = alarmViewModel.currentAlarmData.value.Tue
        AppClass.alarmViewModel.currentAlarmWed.value = alarmViewModel.currentAlarmData.value.Wed
        AppClass.alarmViewModel.currentAlarmThu.value = alarmViewModel.currentAlarmData.value.Thu
        AppClass.alarmViewModel.currentAlarmFri.value = alarmViewModel.currentAlarmData.value.Fri
        AppClass.alarmViewModel.currentAlarmSat.value = alarmViewModel.currentAlarmData.value.Sat
        AppClass.alarmViewModel.currentAlarmSun.value = alarmViewModel.currentAlarmData.value.Sun
        AppClass.alarmViewModel.currentAlarmVibration.value = alarmViewModel.currentAlarmData.value.vibration
        AppClass.alarmViewModel.currentAlarmRequestCode.value = alarmViewModel.currentAlarmData.value.requestCode
        AppClass.alarmViewModel.currentAlarmMode.value = alarmViewModel.currentAlarmData.value.mode
        AppClass.alarmViewModel.currentAlarmHour.value = alarmViewModel.currentAlarmData.value.hour
        AppClass.alarmViewModel.currentAlarmMin.value = alarmViewModel.currentAlarmData.value.minute
        AppClass.alarmViewModel.currentAlarmVolume.value = alarmViewModel.currentAlarmData.value.volume
        AppClass.alarmViewModel.currentAlarmBell.value = alarmViewModel.currentAlarmData.value.bell
        AppClass.alarmViewModel.currentAlarmName.value = alarmViewModel.currentAlarmData.value.name

        Log.d("AlarmGroupie", "name: ${AppClass.alarmViewModel.currentAlarmName.value}")

        val intent = Intent(context, NormalAlarmActivity::class.java)
        context.startActivity(intent)
    }

    override fun getLayout(): Int {
        return R.layout.alarm_item
    }
}