package com.easyo.pairalarm.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_data")
data class AlarmData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "button") var button: Boolean,
    @ColumnInfo(name = "Sun") var Sun: Boolean,
    @ColumnInfo(name = "Mon") var Mon: Boolean,
    @ColumnInfo(name = "Tue") var Tue: Boolean,
    @ColumnInfo(name = "Wed") var Wed: Boolean,
    @ColumnInfo(name = "Thu") var Thu: Boolean,
    @ColumnInfo(name = "Fri") var Fri: Boolean,
    @ColumnInfo(name = "Sat") var Sat: Boolean,
    @ColumnInfo(name = "hour") var hour: Int,
    @ColumnInfo(name = "minute") var minute: Int,
    @ColumnInfo(name = "volume") var volume: Int,
    @ColumnInfo(name = "vibration") var vibration: Int,
    @ColumnInfo(name = "quick") var quick: Boolean,
    @ColumnInfo(name = "bell") var bell: Int,
    @ColumnInfo(name = "mode") var mode: Int,
    @ColumnInfo(name = "name") var name: String,
)