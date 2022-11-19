package com.easyo.pairalarm.extensions

import android.view.View

var lastClickTime = 0L
fun View.setOnSingleClickListener(onClick: (view: View) -> Unit) {
    setOnClickListener {
        if (lastClickTime < System.currentTimeMillis() - 500) {
            lastClickTime = System.currentTimeMillis()
            onClick(this)
        }
    }
}
