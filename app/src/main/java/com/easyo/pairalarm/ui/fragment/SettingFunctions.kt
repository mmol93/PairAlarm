package com.easyo.pairalarm.ui.fragment

interface SettingFunctions {

    val title: String

    fun setQuickAlarmBell(title: String)
    fun setQuickAlarmMode(title: String)
    fun openAppInfo()
    fun reportAboutApp()
}