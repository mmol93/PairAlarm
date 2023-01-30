package com.easyo.pairalarm.groupieitem

import android.content.Context
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.SettingItemBinding
import com.xwray.groupie.databinding.BindableItem

class SettingListAdapter(
    private val context: Context,
    private val title: String,
    private val toggleButton: Boolean? = null
) : BindableItem<SettingItemBinding>(title.hashCode().toLong()) {
    override fun bind(binding: SettingItemBinding, position: Int) {
        binding.settingSubItemText.text = title
    }

    override fun getLayout(): Int = R.layout.setting_item
}