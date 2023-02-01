package com.easyo.pairalarm.groupieitem

import android.content.Context
import androidx.core.content.ContextCompat
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.SettingItemBinding
import com.easyo.pairalarm.model.SettingContentType
import com.xwray.groupie.databinding.BindableItem

class SettingContentItem(
    private val context: Context,
    private val title: String,
    private val settingContentType: SettingContentType,
    private val onlyHasTwoData: Boolean = false,
) : BindableItem<SettingItemBinding>(title.hashCode().toLong()) {
    override fun bind(binding: SettingItemBinding, position: Int) {
        binding.title = title

        when (settingContentType) {
            SettingContentType.SINGLE -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(context, R.drawable.item_small_rounded_corner_clear)
            }
            SettingContentType.FIRST -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(context, R.drawable.item_uppper_small_rounded_corner_clear)
                binding.isFirstItem = !onlyHasTwoData
            }
            SettingContentType.LAST -> {
                binding.settingItemLayout.background = ContextCompat.getDrawable(context, R.drawable.item_under_small_rounded_corner_clear)
                binding.isLastItem = true
            }
        }
    }

    override fun getLayout(): Int = R.layout.setting_item
}