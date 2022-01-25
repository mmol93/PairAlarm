package com.EasyO.pairalarm.database.table

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarm_data")
data class AlarmDB (
    @PrimaryKey()
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "switch")
    val switch: Boolean,
    @ColumnInfo(name = "Sun")
    val Sun: Boolean,
    @ColumnInfo(name = "Mon")
    val Mon: Boolean,
    @ColumnInfo(name = "Tue")
    val Tue: Boolean,
    @ColumnInfo(name = "Wed")
    val Wed: Boolean,
    @ColumnInfo(name = "Thu")
    val Thu: Boolean,
    @ColumnInfo(name = "Fri")
    val Fri: Boolean,
    @ColumnInfo(name = "Sat")
    val Sat: Boolean,
    @ColumnInfo(name = "hour")
    val hour: Int,
    @ColumnInfo(name = "minute")
    val minute: Int,
    @ColumnInfo(name = "volume")
    val volume: Int,
    @ColumnInfo(name = "vibration")
    val vibration: Boolean,
    @ColumnInfo(name = "quick")
    val quick: Boolean,
    @ColumnInfo(name = "bell")
    val bell: Int,
    @ColumnInfo(name = "mode")
    val mode: Int,
)