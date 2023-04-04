package com.easyo.pairalarm.recyclerItem

import com.easyo.pairalarm.model.UserGuideContents

interface UserGuideFunctions {
    val userGuideContents: UserGuideContents

    fun openNotificationPermissionSetting()

    fun openBatteryOptimize()
}