package com.easyo.pairalarm.extensions

import android.app.KeyguardManager
import android.content.Context
import android.os.Build
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.easyo.pairalarm.R
import com.easyo.pairalarm.model.Failure
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

// 현재 화면이 자동으로 꺼지지 않게 유지 & 잠금화면에 액티비티 띄우기
fun AppCompatActivity.displayOn() {
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        (getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager).apply {
            requestDismissKeyguard(this@displayOn, null)
        }
    } else {
        window.addFlags(
            WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                    WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                    WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
        )
    }
}

// 배경 화면을 클릭하면 현재 Focus되어있는거 클리어하기
fun AppCompatActivity.clearKeyBoardFocus(rootView: View) {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(rootView.windowToken, 0)

    rootView.requestFocus()
}

// 에러 메시지 표시
fun AppCompatActivity.showErrorSnackBar(view: View, error: Failure) {
    this.let { Snackbar.make(view, R.string.some_error, Snackbar.LENGTH_SHORT) }
        .also { it.show() }
    Timber.e("error: ${error.error.message}")
}