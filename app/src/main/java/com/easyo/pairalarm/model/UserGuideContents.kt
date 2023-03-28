package com.easyo.pairalarm.model

import android.content.Context
import androidx.annotation.StringRes
import com.easyo.pairalarm.R

enum class UserGuideContents(@StringRes val titleRes: Int) {
    BACKGROUND_SETTING(R.string.background_white_list),
    BATTERY_OPTIMIZE(R.string.battery_optimize)
    ;

    fun getTitle(context: Context) = context.getString(this.titleRes)
}