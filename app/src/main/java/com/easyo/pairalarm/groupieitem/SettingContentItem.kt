package com.easyo.pairalarm.groupieitem

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.easyo.pairalarm.BuildConfig
import com.easyo.pairalarm.R
import com.easyo.pairalarm.dataStore.DataStoreTool
import com.easyo.pairalarm.databinding.SettingItemBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.model.*
import com.easyo.pairalarm.ui.activity.DebugActivity
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
        // 레이아웃의 배경 셋팅
        when (settingContentType) {
            SettingContentType.SINGLE -> {
                binding.settingItemLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.item_small_rounded_corner)
                binding.isLastItem = true
            }
            SettingContentType.FIRST -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(
                    context, R.drawable.item_setting_uppper_small_rounded_corner
                )
            }
            SettingContentType.LAST -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(
                    context, R.drawable.item_setting_under_small_rounded_corner
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
                SettingContents.QUICKALARM_MUTE -> {
                    setQuickAlarmMute(settingContents.title)
                }
                SettingContents.APP_INFO -> {
                    openAppInfo()
                    if (BuildConfig.DEBUG){
                        binding.root.setOnLongClickListener {
                            val intent = Intent(context, DebugActivity::class.java)
                            context.startActivity(intent)
                            true
                        }
                    }
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

    // 각 override fun에는 클릭 시 실시한 행동 정의

    override fun setQuickAlarmBell(key: String) {
        BellSelectDialogFragment(settingDetail.getBellIndex()) { selectedBellIndex ->
            launch {
                saveStringData(key, selectedBellIndex.getBellName())
            }
        }.also { it.show((context as AppCompatActivity).supportFragmentManager, null) }
    }

    override fun setQuickAlarmMode(key: String) {
        SimpleDialog.showAlarmModeDialog(
            context,
            clickedItemPosition = settingDetail.getModeIndex(),
            positive = { dialogInterface ->
                launch {
                    val alert = dialogInterface as AlertDialog
                    when (alert.listView.checkedItemPosition) {
                        // Normal 클릭 시
                        0 -> {
                            saveStringData(key, AlarmMode.NORMAL.mode)
                        }
                        // Calculate 클릭 시
                        1 -> {
                            saveStringData(key, AlarmMode.CALCULATION.mode)
                        }
                    }
                }
            }
        )
    }

    override fun setQuickAlarmMute(key: String) {
        launch {
            // 클릭할 때 마다 다음 모드가 저장되게 한다.
            when(settingDetail) {
                AlarmMuteOption.MUTE.muteOptionName -> {
                    saveStringData(key, AlarmMuteOption.ONCE.muteOptionName)
                }
                AlarmMuteOption.ONCE.muteOptionName -> {
                    saveStringData(key, AlarmMuteOption.SOUND.muteOptionName)
                }
                AlarmMuteOption.SOUND.muteOptionName -> {
                    saveStringData(key, AlarmMuteOption.MUTE.muteOptionName)
                }
                else -> {
                    saveStringData(key, AlarmMuteOption.MUTE.muteOptionName)
                }
            }
        }
    }

    override fun openAppInfo() {

    }

    override fun reportAboutApp() {

    }

    override fun getLayout(): Int = R.layout.setting_item
}