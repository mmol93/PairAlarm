package com.easyo.pairalarm.model

enum class SettingContents(val title: String) {
    QUICKALARM_BELL("QuickAlarm Bell"),
    QUICKALARM_MODE("QuickAlarm Mode"),
    BLANK(""),
    APP_INFO("App Info"),
    REPORT("Report")
}

// AlarmBell
typealias BellName = String
typealias BellIndex = Int

enum class BellType(val title: BellName) {
    WALKING("Walking"),
    PINAOMAN("PianoMan"),
    HAPPYTOWN("HappyTown"),
    LONELY("Lonely")
}

fun BellName.getBellIndex(): Int {
    return when (this) {
        BellType.WALKING.title -> 0
        BellType.PINAOMAN.title -> 1
        BellType.HAPPYTOWN.title -> 2
        BellType.LONELY.title -> 3
        else -> 0
    }
}

fun BellIndex.getBellName(): String {
    return when (this) {
        0 -> BellType.WALKING.title
        1 -> BellType.PINAOMAN.title
        2 -> BellType.HAPPYTOWN.title
        3 -> BellType.LONELY.title
        else -> BellType.WALKING.title
    }
}


// AlarmMode
typealias AlarmModeName = String

enum class AlarmMode(val mode: AlarmModeName) {
    NORMAL("Normal"),
    CALCULATION("Calculation")
}

fun AlarmModeName.getModeIndex(): Int {
    return when (this) {
        AlarmMode.NORMAL.mode -> 0
        AlarmMode.CALCULATION.mode -> 1
        else -> 0
    }
}