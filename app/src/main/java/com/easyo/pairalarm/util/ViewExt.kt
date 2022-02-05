package com.easyo.pairalarm.util

import android.view.View

var lastClickTime = 0L
fun View.setOnSingleClickExt(onClick: (view: View) -> Unit) {
    setOnClickListener {
        if (lastClickTime < System.currentTimeMillis() - 1000) {
            lastClickTime = System.currentTimeMillis()
            onClick(this)
        }
    }
}