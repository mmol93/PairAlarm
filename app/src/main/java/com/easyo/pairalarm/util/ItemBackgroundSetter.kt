package com.easyo.pairalarm.util

import android.content.Context
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.easyo.pairalarm.R
import com.easyo.pairalarm.model.RecyclerItemContentsType

fun LinearLayout.itemBackgroundSetter(
    recyclerItemContentsType: RecyclerItemContentsType?,
    context: Context,
    isLastItem: Boolean = false
): Boolean {
    when (recyclerItemContentsType) {
        RecyclerItemContentsType.SINGLE -> {
            background =
                ContextCompat.getDrawable(context, R.drawable.item_small_rounded_corner)
            return isLastItem
        }
        RecyclerItemContentsType.FIRST -> {
            background = ContextCompat.getDrawable(
                context, R.drawable.item_setting_uppper_small_rounded_corner
            )
            return false
        }
        RecyclerItemContentsType.LAST -> {
            background = ContextCompat.getDrawable(
                context, R.drawable.item_setting_under_small_rounded_corner
            )
            return isLastItem
        }
        else -> {
            return false
        }
    }
}