package com.easyo.pairalarm.ui.dialog

import android.content.Context
import android.content.DialogInterface
import com.easyo.pairalarm.R
import androidx.appcompat.app.AlertDialog

object SimpleDialog {
    fun make(
        context: Context,
        title: String,
        message: String,
        icon: Int? = null,
        positive: ((dialogInterface: DialogInterface) -> Unit),
        negative: (() -> Unit)? = null
    ) {
        setUpSimpleDialog(
            context,
            title,
            message,
            icon = icon,
            positive = positive,
            negative = negative
        )
    }

    fun make(
        context: Context,
        title: String,
        messageItems: Array<String>,
        clickedItemPosition: Int = 0,
        icon: Int? = null,
        positive: ((dialogInterface: DialogInterface) -> Unit),
        negative: (() -> Unit)? = null
    ) {
        setUpSimpleDialog(
            context,
            title,
            messageItems = messageItems,
            clickedItemPosition = clickedItemPosition,
            icon = icon,
            positive = positive,
            negative = negative
        )
    }

    private fun setUpSimpleDialog(
        context: Context,
        title: String,
        message: String? = null,
        messageItems: Array<String>? = null,
        clickedItemPosition: Int = 0,
        icon: Int? = null,
        positive: ((dialogInterface: DialogInterface) -> Unit),
        negative: (() -> Unit)? = null
    ) {
        val dialogBuilder = AlertDialog.Builder(context)

        dialogBuilder.setTitle(title)
        if (messageItems.isNullOrEmpty()) {
            dialogBuilder.setMessage(message)
        } else {
            dialogBuilder.setSingleChoiceItems(
                messageItems,
                clickedItemPosition,
                null
            )
        }

        if (icon != null) {
            dialogBuilder.setIcon(icon)
        }

        // Dialog의 No 버튼 - 주로 Toast 메시지 출력
        dialogBuilder.setNegativeButton(context.getString(R.string.no)) { _: DialogInterface, _: Int ->
            negative?.invoke()
        }

        // Dialog의 Yes 버튼 - 주로 특정 메서드 실행
        dialogBuilder.setNeutralButton(context.getString(R.string.yes)) { dialogInterface: DialogInterface, _: Int ->
            positive.invoke(dialogInterface)
        }

        // Dialog를 취소했을 때도 No랑 똑같이 취급
        dialogBuilder.setOnCancelListener {
            negative?.invoke()
        }

        dialogBuilder.show()
    }
}
