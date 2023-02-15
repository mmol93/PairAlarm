package com.easyo.pairalarm.ui.fragment

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import autoCleared
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentAlarmBinding
import com.easyo.pairalarm.extensions.getPermissionActivityResultLauncher
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.extensions.showErrorSnackBar
import com.easyo.pairalarm.groupieitem.AlarmItem
import com.easyo.pairalarm.ui.activity.NormalAlarmSetActivity
import com.easyo.pairalarm.ui.activity.SimpleAlarmSetActivity
import com.easyo.pairalarm.ui.dialog.SimpleDialog
import com.easyo.pairalarm.util.cancelAlarmNotification
import com.easyo.pairalarm.util.getNextAlarm
import com.easyo.pairalarm.util.makeAlarmNotification
import com.easyo.pairalarm.util.makeToast
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.xwray.groupie.GroupieAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class AlarmFragment : Fragment(R.layout.fragment_alarm) {
    private var binding: FragmentAlarmBinding by autoCleared()
    private lateinit var alarmActivityIntent: Intent
    private val alarmViewModel: AlarmViewModel by activityViewModels()
    private val permissionRequest = getPermissionActivityResultLauncher(
        allGranted = {
            // 모든 권한이 확인되어 있을 때
            if (checkOverlayPermission() && ::alarmActivityIntent.isInitialized) {
                startActivity(alarmActivityIntent)
            }
        },
        notGranted = {
            // 1개라도 허락되지 않은 권한이 있을 때
            SimpleDialog.showSimpleDialog(
                requireContext(),
                getString(R.string.dialog_permission_title),
                getString(R.string.dialog_notification_permission_message),
                positive = { },
                negative = {
                    makeToast(
                        requireContext(),
                        getString(R.string.dialog_notification_permission_message)
                    )
                }
            )
        }
    )

    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            // 오버레이 권한 설정에서 돌아왔을 때
            Timber.d("resultCode: ${result.resultCode}")
            // 오버레이 권한은 설정 후 Back키로 돌아오기 때문에 RESULT_CANCELED가 찍힌다
            if (result.resultCode == Activity.RESULT_CANCELED) {
                SimpleDialog.showSimpleDialog(
                    requireContext(),
                    getString(R.string.dialog_permission_title),
                    getString(R.string.dialog_overlay_message),
                    positive = { checkOverlayPermission() },
                    negative = {
                        makeToast(
                            requireContext(),
                            getString(R.string.dialog_permission_overlay_no)
                        )
                    }
                )
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentAlarmBinding>(view)?.let { binding = it } ?: return
        binding.lifecycleOwner = this

        // TODO: 나중에 화면 사이즈에 맞게 숫자 바뀌게 하기
        val recyclerViewSpan = 2

        // Groupie - RecyclerView 정의
        val alarmRecyclerAdapter = GroupieAdapter()
        binding.alarmRecycler.run {
            adapter = alarmRecyclerAdapter
            layoutManager = GridLayoutManager(context, recyclerViewSpan, GridLayoutManager.VERTICAL, false)
        }

        // Groupie - RecyclerView 데이터 입력
        viewLifecycleOwner.lifecycleScope.launch {
            alarmViewModel.getAllAlarmData().collect { alarmDataList ->
                Timber.d("AlarmData: $alarmDataList")
                alarmDataList.map { AlarmItem(requireContext(), it, alarmViewModel) }
                    .also { alarmRecyclerAdapter.update(it) }

                if (alarmDataList.isEmpty()) {
                    cancelAlarmNotification(requireContext())
                } else {
                    val nextAlarm = getNextAlarm(alarmDataList)
                    makeAlarmNotification(requireContext(), nextAlarm.toString())
                }
            }
        }

        // FAB의 간격 조절
        val metrics = this.resources.displayMetrics
        val interval = when {
            // mdpi
            metrics.densityDpi <= 160 -> 55f
            // hdpi
            metrics.densityDpi <= 240 -> 105f
            // xhdpi
            metrics.densityDpi <= 320 -> 155f
            // xxhdpi
            metrics.densityDpi <= 480 -> 205f
            // xxxhdpi
            else -> 255f

        }

        binding.fabLayout.animationSize = interval

        // 일반 알람 설정
        binding.fab2.setOnSingleClickListener {
            alarmActivityIntent = Intent(activity, NormalAlarmSetActivity::class.java)
            checkEssentialPermission()
        }

        // 간단 알람 설정
        binding.fab3.setOnSingleClickListener {
            alarmActivityIntent = Intent(activity, SimpleAlarmSetActivity::class.java)
            checkEssentialPermission()
        }

        alarmViewModel.failure.observe(viewLifecycleOwner) {
            showErrorSnackBar(it)
        }
    }

    // 필요한 권한 확인
    // TODO: Dialog로 어떤 권한이 왜 필요한지 설명하기
    private fun checkEssentialPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkOverlayPermission()) {
                permissionRequest.launch(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS))
            }
        } else {
            if (checkOverlayPermission()) {
                startActivity(alarmActivityIntent)
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
            resultLauncher.launch(intent)
            false
        } else {
            true
        }
    }
}
