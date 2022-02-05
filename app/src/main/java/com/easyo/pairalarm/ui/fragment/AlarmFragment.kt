package com.easyo.pairalarm.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.easyO.pairalarm.ui.activity.MakeAlarmActivity
import com.easyO.pairalarm.Constant.MAKEALARM
import com.easyO.pairalarm.Constant.OVERLAYCODE
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentAlarmBinding
import com.easyo.pairalarm.util.ControlDialog
import com.easyo.pairalarm.util.makeToast
import com.easyo.pairalarm.util.setOnSingleClickExt

class AlarmFragment : Fragment(R.layout.fragment_alarm) {
    private lateinit var binding: FragmentAlarmBinding

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
            // 오버레이 권한 확인
            if (!Settings.canDrawOverlays(requireContext())) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:${requireContext().packageName}")
                )
                startActivityForResult(intent, OVERLAYCODE)
                Log.d("mainActivity", "오버레이 intent 호출")
            } else {
                val alarmActivity = Intent(activity, MakeAlarmActivity::class.java)
                // 알람 세팅을 위한 액티비티 소환
                // todo 이 부분은 registerForActivityResult로 구현해보기
                startActivityForResult(alarmActivity, MAKEALARM)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 오버레이 권한 설정에서 돌아왔을 때
        if (requestCode == OVERLAYCODE) {
            // todo Dialog 만드는거 함수로 만들어서 간단하게 만들 수 있게 하기
            ControlDialog.make(
                requireContext(),
                "dd",
                "3",
                null,
                positive = {  },
                negative = { makeToast(requireContext(), getString(R.string.dialog_permission_overlay_no))}
            )
        }
    }
}