package com.easyo.pairalarm.groupieitem

import android.content.Context
import androidx.core.content.ContextCompat
import com.easyo.pairalarm.R
import com.easyo.pairalarm.dataStore.DataStoreTool
import com.easyo.pairalarm.databinding.SettingItemBinding
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.model.SettingContentType
import com.easyo.pairalarm.model.SettingContents
import com.easyo.pairalarm.ui.fragment.SettingFunctions
import com.xwray.groupie.databinding.BindableItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SettingContentItem(
    override val context: Context,
    override val title: String,
    private val settingContentType: SettingContentType?,
) : BindableItem<SettingItemBinding>(title.hashCode().toLong()), DataStoreTool, SettingFunctions {

    override fun bind(binding: SettingItemBinding, position: Int) {
        binding.title = title
        when (settingContentType) {
            SettingContentType.SINGLE -> {
                binding.settingItemLayout.background =
                    ContextCompat.getDrawable(context, R.drawable.item_small_rounded_corner_clear)
                binding.isLastItem = true
            }
            SettingContentType.FIRST -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.item_uppper_small_rounded_corner_clear
                )
            }
            SettingContentType.LAST -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(
                    context,
                    R.drawable.item_under_small_rounded_corner_clear
                )
                binding.isLastItem = true
            }
            else -> {

            }
        }

        CoroutineScope(Dispatchers.Main).launch {
            getStoredDataWithFlow(title).collectLatest {
                binding.description = it
            }
        }

        binding.root.setOnSingleClickListener {
            when (title) {
                SettingContents.QUICKALARM_BELL.title -> {
                    setQuickAlarmBell(title)
                }
                SettingContents.QUICKALARM_MODE.title -> {
                    setQuickAlarmMode(title)
                }
                SettingContents.APP_INFO.title -> {
                    openAppInfo()
                }
                SettingContents.REPORT.title -> {
                    reportAboutApp()
                }
            }
        }
    }

    override fun setQuickAlarmBell(title: String) {

    }

    override fun setQuickAlarmMode(title: String) {

    }

    override fun openAppInfo() {

    }

    override fun reportAboutApp() {

    }

    override fun getLayout(): Int = R.layout.setting_item
}