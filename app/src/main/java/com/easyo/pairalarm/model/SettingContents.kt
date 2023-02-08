package com.easyo.pairalarm.model

enum class SettingContents(val title: String) {
    QUICKALARM_BELL("QuickAlarm Bell"),
    QUICKALARM_MODE("QuickAlarm Mode"),
    BLANK(""),
    APP_INFO("App Info"),
    REPORT("Report");
}

enum class BellType(val title: String) {
    WALKING("Walking"),
    PINAOMAN("PianoMan"),
    HAPPYTOWN("HappyTown"),
    LONELY("Lonely")
}

enum class AlarmMode(val mode: String) {
    NORMAL("Normal"),
    CALCULATION("Calculation")
}