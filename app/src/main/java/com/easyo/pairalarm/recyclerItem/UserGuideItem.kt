package com.easyo.pairalarm.recyclerItem

import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ItemSettingBinding
import com.xwray.groupie.databinding.BindableItem

class UserGuideItem(): BindableItem<ItemSettingBinding>() {
    override fun bind(binding: ItemSettingBinding, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getLayout(): Int = R.layout.item_setting
}