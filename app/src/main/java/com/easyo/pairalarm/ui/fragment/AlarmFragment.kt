package com.easyo.pairalarm.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.viewModels
import com.easyo.pairalarm.ui.activity.NormalAlarmActivity
import com.easyo.pairalarm.Constant.OVERLAYCODE
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentAlarmBinding
import com.easyo.pairalarm.ui.activity.SimpleAlarmActivity
import com.easyo.pairalarm.util.ControlDialog
import com.easyo.pairalarm.util.makeToast
import com.easyo.pairalarm.util.setOnSingleClickExt
import com.easyo.pairalarm.viewModel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmFragment : Fragment(R.layout.fragment_alarm) {
    private lateinit var binding: FragmentAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!

        // FAB의 간격 조절
        var interval = 0f
        val metrics = this.resources.displayMetrics
        if (metrics.densityDpi <= 160) {
            // mdpi
            interval = 55f
        } else if (metrics.densityDpi <= 240) {
            // hdpi
            interval = 105f
        } else if (metrics.densityDpi <= 320) {
            // xhdpi
            interval = 155f
        } else if (metrics.densityDpi <= 480) {
            // xxhdpi
            interval = 205f
        } else if (metrics.densityDpi <= 640) {
            // xxxhdpi
            interval = 255f
        }
        binding.fabLayout.animationSize = interval

        // 일반 알람 설정
        binding.fab2.setOnSingleClickExt {
            if (checkOverlayPermission()){

                // todo 밑에 내용은 나중에 recyclerView에서 각 항목을 클릭했을 때 실시하게 한다
//                alarmViewModel.currentAlarmMon.value = alarmViewModel.currentAlarmData.value.Mon
//                alarmViewModel.currentAlarmTue.value = alarmViewModel.currentAlarmData.value.Tue
//                alarmViewModel.currentAlarmWed.value = alarmViewModel.currentAlarmData.value.Wed
//                alarmViewModel.currentAlarmThu.value = alarmViewModel.currentAlarmData.value.Thu
//                alarmViewModel.currentAlarmFri.value = alarmViewModel.currentAlarmData.value.Fri
//                alarmViewModel.currentAlarmSat.value = alarmViewModel.currentAlarmData.value.Sat
//                alarmViewModel.currentAlarmSun.value = alarmViewModel.currentAlarmData.value.Sun
//                alarmViewModel.currentAlarmVibration.value = alarmViewModel.currentAlarmData.value.vibration
//                alarmViewModel.currentAlarmRequestCode.value = alarmViewModel.currentAlarmData.value.requestCode
//                alarmViewModel.currentAlarmMode.value = alarmViewModel.currentAlarmData.value.mode
//                alarmViewModel.currentAlarmHour.value = alarmViewModel.currentAlarmData.value.hour
//                alarmViewModel.currentAlarmMin.value = alarmViewModel.currentAlarmData.value.minute
//                alarmViewModel.currenAlarmVolume.value = alarmViewModel.currentAlarmData.value.volume
//                alarmViewModel.currentAlarmBell.value = alarmViewModel.currentAlarmData.value.bell
//                alarmViewModel.currentAlarmName.value = alarmViewModel.currentAlarmData.value.name

                val makeNormalAlarmIntent = Intent(activity, NormalAlarmActivity::class.java)
                startActivity(makeNormalAlarmIntent)
            }

        }

        // 간단 알람 설정
        binding.fab3.setOnSingleClickExt {
            if (checkOverlayPermission()){
                val makeSimpleAlarmIntent = Intent(activity, SimpleAlarmActivity::class.java)
                startActivity(makeSimpleAlarmIntent)
            }
        }
    }

    // 오버레이 권한 확인
    private fun checkOverlayPermission(): Boolean{
        // 권한이 ok가 아닐 때
        if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
            startActivityForResult(intent, OVERLAYCODE)
            Log.d("mainActivity", "오버레이 intent 호출")
            return false
        } else {
            return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 오버레이 권한 설정에서 돌아왔을 때
        if (requestCode == OVERLAYCODE) {
            // todo Dialog 만드는거 함수로 만들어서 간단하게 만들 수 있게 하기
            ControlDialog.make(
                requireContext(),
                getString(R.string.dialog_permission_title),
                getString(R.string.dialog_overlay_message),
                null,
                positive = {  },
                negative = { makeToast(requireContext(), getString(R.string.dialog_permission_overlay_no))}
            )
        }
    }
}