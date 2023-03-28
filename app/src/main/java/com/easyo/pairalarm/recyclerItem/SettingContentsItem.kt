package com.easyo.pairalarm.recyclerItem

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.easyo.pairalarm.BuildConfig
import com.easyo.pairalarm.R
import com.easyo.pairalarm.dataStore.DataStoreTool
import com.easyo.pairalarm.databinding.ItemSettingBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.model.*
import com.easyo.pairalarm.ui.activity.DebugActivity
import com.easyo.pairalarm.ui.activity.WebActivity
import com.easyo.pairalarm.ui.dialog.BellSelectDialogFragment
import com.easyo.pairalarm.ui.dialog.SimpleDialog
import com.easyo.pairalarm.util.LICENSE_LINK
import com.easyo.pairalarm.util.itemBackgroundSetter
import com.xwray.groupie.databinding.BindableItem
import com.xwray.groupie.databinding.GroupieViewHolder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class SettingContentsItem(
    override val context: Context,
    override val settingContents: SettingContents,
    private val recyclerItemContentsType: RecyclerItemContentsType?,
    override val coroutineContext: CoroutineContext,
    override val job: Job,
    private val showGuideFragment: () -> Unit
) : BindableItem<ItemSettingBinding>(settingContents.hashCode().toLong()), DataStoreTool,
    SettingContentsFunctions {
    private lateinit var settingDetail: String

    override fun bind(binding: ItemSettingBinding, position: Int) {
        binding.title = settingContents.getTitle(context)
        // 레이아웃의 배경 셋팅
        when (recyclerItemContentsType) {
            RecyclerItemContentsType.SINGLE -> {
                binding.isLastItem = binding.settingItemLayout.itemBackgroundSetter(
                    RecyclerItemContentsType.SINGLE,
                    context,
                    true
                )
            }
            RecyclerItemContentsType.FIRST -> {
                binding.isLastItem = binding.settingItemLayout.itemBackgroundSetter(
                    RecyclerItemContentsType.FIRST,
                    context,
                    false
                )
            }
            RecyclerItemContentsType.LAST -> {
                binding.isLastItem = binding.settingItemLayout.itemBackgroundSetter(
                    RecyclerItemContentsType.LAST,
                    context,
                    true
                )
            }
            else -> {

            }
        }


        launch {
            getStoredStringDataWithFlow(settingContents.getTitle(context)).collectLatest {
                binding.settingDetail = it
                settingDetail = it
            }
        }

        binding.root.setOnSingleClickListener {
            when (settingContents) {
                SettingContents.QUICKALARM_BELL -> {
                    setQuickAlarmBell(settingContents.getTitle(context))
                }
                SettingContents.QUICKALARM_MODE -> {
                    setQuickAlarmMode(settingContents.getTitle(context))
                }
                SettingContents.QUICKALARM_VIBRATE -> {
                    setQuickAlarmMute(settingContents.getTitle(context))
                }
                SettingContents.APP_INFO -> {
                    openAppInfo()
                    if (BuildConfig.DEBUG) {
                        binding.root.setOnLongClickListener {
                            val intent = Intent(context, DebugActivity::class.java)
                            context.startActivity(intent)
                            true
                        }
                    }
                }
                SettingContents.USER_GUIDE -> {
                    userGuide()
                }
                else -> {}
            }
        }
    }

    override fun onViewDetachedFromWindow(viewHolder: GroupieViewHolder<ItemSettingBinding>) {
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
            when (settingDetail) {
                AlarmVibrationOption.Vibration.vibrationOptionName -> {
                    saveStringData(key, AlarmVibrationOption.ONCE.vibrationOptionName)
                }
                AlarmVibrationOption.ONCE.vibrationOptionName -> {
                    saveStringData(key, AlarmVibrationOption.SOUND.vibrationOptionName)
                }
                AlarmVibrationOption.SOUND.vibrationOptionName -> {
                    saveStringData(key, AlarmVibrationOption.Vibration.vibrationOptionName)
                }
                else -> {
                    saveStringData(key, AlarmVibrationOption.Vibration.vibrationOptionName)
                }
            }
        }
    }

    override fun openAppInfo() {
        val intent = Intent(context, WebActivity::class.java)
        intent.putExtra("title", "License")
        intent.putExtra("url", LICENSE_LINK)
        context.startActivity(intent)
    }

    override fun userGuide() {
        showGuideFragment.invoke()
    }

    override fun getLayout(): Int = R.layout.item_setting
}