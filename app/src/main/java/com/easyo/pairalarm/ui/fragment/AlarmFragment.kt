package com.easyo.pairalarm.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.easyo.pairalarm.AppClass
import com.easyo.pairalarm.ui.activity.NormalAlarmActivity
import com.easyo.pairalarm.Constant.OVERLAYCODE
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.databinding.FragmentAlarmBinding
import com.easyo.pairalarm.groupieitem.AlarmGroupie
import com.easyo.pairalarm.ui.activity.SimpleAlarmActivity
import com.easyo.pairalarm.util.ControlDialog
import com.easyo.pairalarm.util.initCurrentAlarmData
import com.easyo.pairalarm.util.makeToast
import com.easyo.pairalarm.util.setOnSingleClickExt
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AlarmFragment : Fragment(R.layout.fragment_alarm) {
    private lateinit var binding: FragmentAlarmBinding
    private val alarmViewModel: AlarmViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!

        // 나중에 화면 사이즈에 맞게 숫자 바뀌게 하기
        var recyclerViewSpan = 2

        // Groupie - RecyclerView 정의
        val alarmRecyclerAdapter = GroupieAdapter()
        binding.alarmRecycler.run {
            adapter = alarmRecyclerAdapter
            layoutManager = GridLayoutManager(context, recyclerViewSpan, GridLayoutManager.VERTICAL, false)
        }

        // Groupie - RecyclerView 데이터 입력
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                AppClass.alarmDataList?.collectLatest { alarmDataList ->
                    Log.d("AlarmFragment", "AlarmData: $alarmDataList")
                    alarmDataList.map { AlarmGroupie(requireContext(), it, alarmViewModel) }
                        .also { alarmRecyclerAdapter.update(it) }
                }
            }
        }

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
            if (checkOverlayPermission()) {
                initViewModel()
                val makeNormalAlarmIntent = Intent(activity, NormalAlarmActivity::class.java)
                startActivity(makeNormalAlarmIntent)
            }
        }

        // 간단 알람 설정
        binding.fab3.setOnSingleClickExt {
            if (checkOverlayPermission()) {
                initViewModel()
                val makeSimpleAlarmIntent = Intent(activity, SimpleAlarmActivity::class.java)
                startActivity(makeSimpleAlarmIntent)
            }
        }
    }

    private fun initViewModel(){
        alarmViewModel.currentAlarmData.value = AlarmData(
            id = null,
            button = true,
            Sun = false,
            Mon = false,
            Tue = false,
            Wed = false,
            Thu = false,
            Fri = false,
            Sat = false,
            vibration = 0,
            requestCode = 0,
            mode = 0,
            hour = 1,
            minute = 0,
            quick = false,
            volume = 100,
            bell = 0,
            name = ""
        )
        alarmViewModel.currentAlarmId.value = null
        alarmViewModel.currentAlarmName.value = ""
        alarmViewModel.currentAlarmMon.value = false
        alarmViewModel.currentAlarmTue.value = false
        alarmViewModel.currentAlarmWed.value = false
        alarmViewModel.currentAlarmThu.value = false
        alarmViewModel.currentAlarmFri.value = false
        alarmViewModel.currentAlarmSat.value = false
        alarmViewModel.currentAlarmSun.value = false
        alarmViewModel.currentAlarmVibration.value = 0
        alarmViewModel.currentAlarmHour.value = 1
        alarmViewModel.currentAlarmMin.value = 0
        alarmViewModel.currentAlarmBell.value = 0
        alarmViewModel.currentAlarmMode.value = 0
        alarmViewModel.currentAlarmVolume.value = 100
        alarmViewModel.currentAlarmRequestCode.value = null
        alarmViewModel.currentAlarmAmPm.value = 0
        alarmViewModel.playStopTextView.value = "play"

        AppClass.alarmViewModel = alarmViewModel
    }


    // 오버레이 권한 확인
    private fun checkOverlayPermission(): Boolean {
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
                positive = { },
                negative = {
                    makeToast(
                        requireContext(),
                        getString(R.string.dialog_permission_overlay_no)
                    )
                }
            )
        }
    }
}