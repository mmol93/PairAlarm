package com.easyo.pairalarm.recyclerItem

import android.content.Context
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ItemSettingBinding
import com.easyo.pairalarm.model.RecyclerItemContentsType
import com.easyo.pairalarm.model.UserGuideContents
import com.easyo.pairalarm.util.itemBackgroundSetter
import com.xwray.groupie.databinding.BindableItem
import timber.log.Timber

class UserGuideItem(
    private val context: Context,
    private val recyclerItemContentsType: RecyclerItemContentsType?,
    override val userGuideContents: UserGuideContents
): BindableItem<ItemSettingBinding>(userGuideContents.hashCode().toLong()), UserGuideFunctions {
    override fun bind(binding: ItemSettingBinding, position: Int) {
        binding.title = userGuideContents.getTitle(context)
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
        binding.root.setOnClickListener {
            when (userGuideContents) {
                UserGuideContents.BACKGROUND_LIMIT -> {
                    openBackgroundWhiteList()
                }
                UserGuideContents.BATTERY_OPTIMIZE -> {
                    openBatteryOptimize()
                }
            }
        }
    }

    override fun getLayout(): Int = R.layout.item_setting

    override fun openBackgroundWhiteList() {
        Timber.d("openBackgroundWhiteList clicked")
    }

    override fun openBatteryOptimize() {
        Timber.d("openBatteryOptimize clicked")
    }
}