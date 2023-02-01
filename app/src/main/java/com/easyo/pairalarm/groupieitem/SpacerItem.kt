package com.easyo.pairalarm.groupieitem

import androidx.annotation.DimenRes
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.SpacerItemBinding
import com.xwray.groupie.databinding.BindableItem

class SpacerItem(
    @DimenRes private val space: Int
) : BindableItem<SpacerItemBinding>(space.hashCode().toLong()) {
    companion object {
        /**
         * 4dp
         */
        fun small() = SpacerItem(R.dimen.element_spacing_small)

        /**
         * 8dp
         */
        fun normal() = SpacerItem(R.dimen.element_spacing_normal)

        /**
         * 12dp
         */
        fun xnormal() = SpacerItem(R.dimen.element_spacing_xnormal)

        /**
         * 16dp
         */
        fun large() = SpacerItem(R.dimen.element_spacing_large)

        /**
         * 24dp
         */
        fun xlarge() = SpacerItem(R.dimen.element_spacing_xlarge)

        /**
         * 32dp
         */
        fun xxlarge() = SpacerItem(R.dimen.element_spacing_xxlarge)

        /**
         * 40dp
         */
        fun xxxlarge() = SpacerItem(R.dimen.element_spacing_xxxlarge)
    }

    override fun bind(binding: SpacerItemBinding, position: Int) {
        val params = binding.spacer.layoutParams.apply {
            height = binding.root.resources.getDimensionPixelOffset(space)
        }
        binding.spacer.layoutParams = params
    }

    override fun getLayout(): Int = R.layout.spacer_item
}