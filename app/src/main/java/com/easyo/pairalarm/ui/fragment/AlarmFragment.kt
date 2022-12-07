package com.easyo.pairalarm.ui.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.work.*
import com.easyo.pairalarm.ui.activity.NormalAlarmActivity
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentAlarmBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.groupieitem.AlarmGroupie
import com.easyo.pairalarm.ui.activity.SimpleAlarmActivity
import com.easyo.pairalarm.util.*
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.easyo.pairalarm.worker.NextAlarmWorker
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AlarmFragment : Fragment(R.layout.fragment_alarm) {
    private lateinit var binding: FragmentAlarmBinding
    private val alarmViewModel: AlarmViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DataBindingUtil.bind(view)!!

        // TODO: 나중에 화면 사이즈에 맞게 숫자 바뀌게 하기
        val recyclerViewSpan = 2

        // Groupie - RecyclerView 정의
        val alarmRecyclerAdapter = GroupieAdapter()
        binding.alarmRecycler.run {
            adapter = alarmRecyclerAdapter
            layoutManager =
                GridLayoutManager(context, recyclerViewSpan, GridLayoutManager.VERTICAL, false)
        }

        // Groupie - RecyclerView 데이터 입력
        lifecycleScope.launch {
            alarmViewModel.getAllAlarmData().collectLatest { alarmDataList ->
                Timber.d("AlarmData: $alarmDataList")
                alarmDataList.map { AlarmGroupie(requireContext(), it, alarmViewModel) }
                    .also { alarmRecyclerAdapter.update(it) }

                if (alarmDataList.isEmpty()) {
                    cancelAlarmNotification(requireContext())
                } else {
                    val alarmTimeWorkRequest: WorkRequest =
                        OneTimeWorkRequestBuilder<NextAlarmWorker>()
                            .build()
                    WorkManager.getInstance(requireContext())
                        .enqueueUniqueWork(
                            "makeNotification",
                            ExistingWorkPolicy.KEEP,
                            alarmTimeWorkRequest as OneTimeWorkRequest
                        )
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
        binding.fab2.setOnSingleClickListener {
            if (checkOverlayPermission()) {
                val makeNormalAlarmIntent = Intent(activity, NormalAlarmActivity::class.java)
                startActivity(makeNormalAlarmIntent)
            }
        }

        // 간단 알람 설정
        binding.fab3.setOnSingleClickListener {
            if (checkOverlayPermission()) {
                val makeSimpleAlarmIntent = Intent(activity, SimpleAlarmActivity::class.java)
                startActivity(makeSimpleAlarmIntent)
            }
        }
    }

    // 오버레이 권한 확인
    private fun checkOverlayPermission(): Boolean {
        // 권한이 ok가 아닐 때
        return if (!Settings.canDrawOverlays(requireContext())) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${requireContext().packageName}")
            )
            startActivityForResult(intent, OVERLAY_CODE)
            Timber.d("오버레이 intent 호출")
            false
        } else {
            true
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // 오버레이 권한 설정에서 돌아왔을 때
        if (requestCode == OVERLAY_CODE) {
            SimpleDialog.make(
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
