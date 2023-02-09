package com.easyo.pairalarm.groupieitem

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.easyo.pairalarm.R
import com.easyo.pairalarm.dataStore.DataStoreTool
import com.easyo.pairalarm.databinding.SettingItemBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.model.*
import com.easyo.pairalarm.ui.dialog.BellSelectDialogFragment
import com.easyo.pairalarm.ui.dialog.SimpleDialog
import com.easyo.pairalarm.ui.fragment.SettingFunctions
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SettingContentItem(
    override val context: Context,
    override val settingContents: SettingContents,
    private val settingContentType: SettingContentType?,
    override val coroutineContext: CoroutineContext,
    override val job: Job,
) : BindableItem<SettingItemBinding>(settingContents.hashCode().toLong()), DataStoreTool,
    SettingFunctions {
    private lateinit var settingDetail: String

    override fun bind(binding: SettingItemBinding, position: Int) {
        binding.title = settingContents.title
        when (settingContentType) {
            SettingContentType.SINGLE -> {
                binding.settingItemLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.item_small_rounded_corner_clear)
                binding.isLastItem = true
            }
            SettingContentType.FIRST -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(
                    context, R.drawable.item_uppper_small_rounded_corner_clear
                )
            }
            SettingContentType.LAST -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(
                    context, R.drawable.item_under_small_rounded_corner_clear
                )
                binding.isLastItem = true
            }
            else -> {

            }
        }

        launch {
            getStoredStringDataWithFlow(settingContents.title).collectLatest {
                binding.settingDetail = it
                settingDetail = it
            }
        }

        binding.root.setOnSingleClickListener {
            when (settingContents) {
                SettingContents.QUICKALARM_BELL -> {
                    setQuickAlarmBell(settingContents.title)
                }
                SettingContents.QUICKALARM_MODE -> {
                    setQuickAlarmMode(settingContents.title)
                }
                SettingContents.APP_INFO -> {
                    openAppInfo()
                }
                SettingContents.REPORT -> {
                    reportAboutApp()
                }
                else -> {

                }
            }
        }
    }

    override fun onViewDetachedFromWindow(viewHolder: GroupieViewHolder<SettingItemBinding>) {
        super.onViewDetachedFromWindow(viewHolder)
        job.cancel()
    }

    override fun setQuickAlarmBell(title: String) {
        BellSelectDialogFragment(settingDetail.getBellIndex()) { selectedBellIndex ->
            launch {
                saveStringData(title, selectedBellIndex.getBellName())
            }
        }.also { it.show((context as AppCompatActivity).supportFragmentManager, null) }
    }

    override fun setQuickAlarmMode(title: String) {
        SimpleDialog.make(
            context,
            context.getString(R.string.alarmSet_selectBellDialogTitle),
            AlarmMode.values().map { it.mode }.toTypedArray(),
            settingDetail.getModeIndex(),
            positive = { dialogInterface ->
                launch {
                    val alert = dialogInterface as AlertDialog
                    when (alert.listView.checkedItemPosition) {
                        // Normal 클릭 시
                        0 -> {
                            saveStringData(title, AlarmMode.NORMAL.mode)
                        }
                        // Calculate 클릭 시
                        1 -> {
                            saveStringData(title, AlarmMode.CALCULATION.mode)
                        }
                    }
                }
            }
        )
    }

    override fun openAppInfo() {

    }

    override fun reportAboutApp() {

    }

    override fun getLayout(): Int = R.layout.setting_item
}