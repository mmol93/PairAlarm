package com.easyo.pairalarm.util

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.easyo.pairalarm.R

object SimpleDialog {
    fun make(context: Context,
             title: String,
             message: String,
             icon: Int?,
             positive:(() -> Unit),
             negative:(() -> Unit)
    ) {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle(title)
        dialogBuilder.setMessage(message)
        if (icon != null) {
            dialogBuilder.setIcon(icon)
        }

        // Dialog의 No 버튼 - 주로 Toast 메시지 출력
        dialogBuilder.setNegativeButton(context.getString(R.string.no)) { _: DialogInterface, _: Int ->
            // Toast의 실제 메시지는 여기서 넣는게 아니라 메서드를 정의하는 쪽에서 직접 정의한다
            negative.invoke()
        }

        // Dialog의 Yes 버튼 - 주로 특정 메서드 실행
        dialogBuilder.setNeutralButton(context.getString(R.string.yes)){ _: DialogInterface, _: Int ->
            positive.invoke()
        }

        // Dialog를 취소했을 때도 No랑 똑같이 취급
        dialogBuilder.setOnCancelListener {
            negative.invoke()
        }

        dialogBuilder.show()
    }
}
