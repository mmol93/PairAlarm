package com.easyo.pairalarm.recyclerItem

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.databinding.AlarmItemBinding
import com.easyo.pairalarm.extensions.lastClickTime
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.ui.activity.NormalAlarmSetActivity
import com.easyo.pairalarm.ui.dialog.SimpleDialog
import com.easyo.pairalarm.util.ALARM_CODE_TEXT
import com.easyo.pairalarm.viewModel.AlarmViewModel
import com.xwray.groupie.databinding.BindableItem
import timber.log.Timber

class AlarmItem(
    val context: Context,
    val alarmData: AlarmData,
    private val alarmViewModel: AlarmViewModel
) : BindableItem<AlarmItemBinding>(alarmData.hashCode().toLong()) {

    @SuppressLint("SetTextI18n")
    override fun bind(binding: AlarmItemBinding, position: Int) {
        binding.alarmData = alarmData

        binding.root.setOnSingleClickListener {
            openNormalAlarmActivity()
        }

        // 삭제 버튼 클릭
        binding.deleteImage.setOnSingleClickListener {
            SimpleDialog.showSimpleDialog(
                context,
                title = context.getString(R.string.dialog_delete_title),
                message = context.getString(R.string.dialog_delete_content),
                positive = {
                    alarmViewModel.deleteAlarmData(alarmData)
                }
            )
        }

        // on/off
        binding.onOffSwitch.isChecked = alarmData.alarmIsOn

        binding.onOffSwitch.setOnCheckedChangeListener { _, isChecked ->
            // 한 번만 클릭되는 기능을 넣지 않으면 혼자서 여러번 클릭됨
            if (lastClickTime < System.currentTimeMillis() - 500) {
                lastClickTime = System.currentTimeMillis()
                alarmData.alarmIsOn = isChecked
                alarmViewModel.updateAlarmData(alarmData)
                Timber.d("update alarmData: $alarmData")
            }
        }
        binding.executePendingBindings()
    }

    private fun openNormalAlarmActivity() {
        val intent = Intent(context, NormalAlarmSetActivity::class.java)
        intent.putExtra(ALARM_CODE_TEXT, alarmData.alarmCode)
        context.startActivity(intent)
    }

    override fun getLayout(): Int {
        return R.layout.alarm_item
    }
}
