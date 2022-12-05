package com.easyo.pairalarm.extensions

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity

// 현재 화면이 자동으로 꺼지지 않게 유지 & 잠금화면에 액티비티 띄우기
fun AppCompatActivity.displayOn(){
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1){
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        keyguardManager.requestDismissKeyguard(this, null)
    }else{
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }
}
