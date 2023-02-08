package com.easyo.pairalarm.groupieitem

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.easyo.pairalarm.R
import com.easyo.pairalarm.dataStore.DataStoreTool
import com.easyo.pairalarm.databinding.SettingItemBinding
import com.easyo.pairalarm.extensions.getBellDescription
import com.easyo.pairalarm.extensions.getBellIndex
import com.easyo.pairalarm.extensions.setOnSingleClickListener
import com.easyo.pairalarm.model.AlarmMode
import com.easyo.pairalarm.model.SettingContentType
import com.easyo.pairalarm.model.SettingContents
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
    override val title: String,
    private val settingContentType: SettingContentType?,
    override val coroutineContext: CoroutineContext,
    override val job: Job,
) : BindableItem<SettingItemBinding>(title.hashCode().toLong()), DataStoreTool, SettingFunctions {
    private lateinit var description: String

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
            getStoredStringDataWithFlow(title).collectLatest {
                binding.description = it
                description = it
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

    override fun onViewDetachedFromWindow(viewHolder: GroupieViewHolder<SettingItemBinding>) {
        super.onViewDetachedFromWindow(viewHolder)
        job.cancel()
    }

    override fun setQuickAlarmBell(title: String) {
        BellSelectDialogFragment(description.getBellIndex()) { selectedBellIndex ->
            launch {
                saveStringData(title, selectedBellIndex.getBellDescription())
            }
        }.also { it.show((context as AppCompatActivity).supportFragmentManager, null) }
    }

    override fun setQuickAlarmMode(title: String) {

    }

    override fun openAppInfo() {

    }

    override fun reportAboutApp() {

    }

    override fun getLayout(): Int = R.layout.setting_item
}