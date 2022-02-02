package com.easyo.pairalarm.extensions

import android.view.View

var lastClickTime = 0L
fun View.setOnClickExt(onClick: View.OnClickListener) {

}
fun View.setOnClickExt(onClick: (view: View) -> Unit) {
    setOnClickListener {
        if (lastClickTime < System.currentTimeMillis() - 1000) {
            lastClickTime = System.currentTimeMillis()
            onClick(this)
        }
    }
}