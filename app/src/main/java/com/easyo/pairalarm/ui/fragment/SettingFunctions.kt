package com.easyo.pairalarm.ui.fragment

import com.easyo.pairalarm.model.SettingContents

interface SettingFunctions {

    val settingContents: SettingContents

    fun setQuickAlarmBell(key: String)
    fun setQuickAlarmMode(key: String)
    fun setQuickAlarmMute(key: String)
    fun openAppInfo()
    fun userGuide()
}