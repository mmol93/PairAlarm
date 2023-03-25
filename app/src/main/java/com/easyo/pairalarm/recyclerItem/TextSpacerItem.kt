package com.easyo.pairalarm.recyclerItem

import androidx.annotation.StringRes
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.TextSpacerItemBinding
import com.xwray.groupie.databinding.BindableItem

class TextSpacerItem(
    @StringRes private val listTitle: Int
) : BindableItem<TextSpacerItemBinding>() {
    override fun bind(binding: TextSpacerItemBinding, position: Int) {
        binding.titleText.text = binding.root.context.getString(listTitle)
    }

    override fun getLayout(): Int = R.layout.text_spacer_item
}