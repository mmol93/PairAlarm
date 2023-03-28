package com.easyo.pairalarm.recyclerItem

import com.easyo.pairalarm.model.SettingContents

interface SettingContentsFunctions {

    val settingContents: SettingContents

    fun setQuickAlarmBell(key: String)
    fun setQuickAlarmMode(key: String)
    fun setQuickAlarmMute(key: String)
    fun openAppInfo()
    fun userGuide()
}