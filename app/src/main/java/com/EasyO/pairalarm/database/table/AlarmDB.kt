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
    @ColumnInfo(name = "week")
    val week: Int,
    @ColumnInfo(name = "hour")
    val hour: Int,
    @ColumnInfo(name = "minute")
    val minute: Int,
    @ColumnInfo(name = "volume")
    val volume: Int,
    @ColumnInfo(name = "quick")
    val quick: Int,
    @ColumnInfo(name = "bell")
    val bell: Int,
    @ColumnInfo(name = "mode")
    val mode: Int,
)