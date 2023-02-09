package com.easyo.pairalarm.ui.fragment

import com.easyo.pairalarm.model.SettingContents

interface SettingFunctions {

    val settingContents: SettingContents

    fun setQuickAlarmBell(title: String)
    fun setQuickAlarmMode(title: String)
    fun openAppInfo()
    fun reportAboutApp()
}