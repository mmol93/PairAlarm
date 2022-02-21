package com.easyo.pairalarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MyReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        // ** 휴대폰을 재부팅 했을 때 -> 모든 알람을 재설정
        if (intent!!.action == "android.intent.action.BOOT_COMPLETED" ||
            intent!!.action == "android.intent.action.QUICKBOOT_POWERON") {

        }
        // 앱을 업데이트 했을 때 -> 모든 알람을 재설정
        else if(intent!!.action == "android.intent.action.MY_PACKAGE_REPLACED"){

        }
        // 그 외의 모든 알람(= 내가 설정한 알람)
        else{

        }
    }
}