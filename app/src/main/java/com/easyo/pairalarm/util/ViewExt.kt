package com.easyo.pairalarm.util

import android.view.View
import com.google.android.material.switchmaterial.SwitchMaterial

var lastClickTime = 0L
fun View.setOnSingleClickExt(onClick: (view: View) -> Unit) {
    setOnClickListener {
        if (lastClickTime < System.currentTimeMillis() - 300) {
            lastClickTime = System.currentTimeMillis()
            onClick(this)
        }
    }
}

fun SwitchMaterial.setOnSingleCheckedChangeListener(onClick: (view: SwitchMaterial, Boolean) -> Unit){
    setOnCheckedChangeListener { _, isChecked ->
        if (lastClickTime < System.currentTimeMillis() - 300) {
            lastClickTime = System.currentTimeMillis()
        }
    }
}