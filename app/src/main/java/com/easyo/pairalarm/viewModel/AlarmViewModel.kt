package com.easyo.pairalarm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val alarmRepository: AlarmRepository): ViewModel() {
    val currentAlarmData = MutableStateFlow<AlarmData>(
        AlarmData(
            id = null,
            button = true,
            Sun = false,
            Mon = false,
            Tue = false,
            Wed = false,
            Thu = false,
            Fri = false,
            Sat = false,
            vibration = 0,
            requestCode = 0,
            mode = 0,
            hour = 1,
            minute = 0,
            quick = false,
            volume = 100,
            bell = 0,
            name = ""
        ))
    val currentAlarmName = MutableStateFlow("")
    val currentAlarmMon = MutableStateFlow(false)
    val currentAlarmTue = MutableStateFlow(false)
    val currentAlarmWed = MutableStateFlow(false)
    val currentAlarmThu = MutableStateFlow(false)
    val currentAlarmFri = MutableStateFlow(false)
    val currentAlarmSat = MutableStateFlow(false)
    val currentAlarmSun = MutableStateFlow(false)
    val currentAlarmVibration = MutableStateFlow(0)
    val currentAlarmHour = MutableStateFlow(1)
    val currentAlarmMin = MutableStateFlow(0)
    val currentAlarmBell = MutableStateFlow(0)
    val currentAlarmMode = MutableStateFlow(0)
    val currenAlarmVolume = MutableStateFlow(100)
    val currentAlarmRequestCode = MutableStateFlow<Long?>(null)
    val currenAlarmAmPm = MutableStateFlow(0)
    val playStopTextView = MutableStateFlow("play")

    // DB에 관한 것들
    fun insert(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.insert(alarmData)
        Log.d("AlarmViewModel", "inserted: $alarmData")
    }

    fun update(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.update(alarmData)
    }

    fun delete(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.delete(alarmData)
    }

    fun getAllAlarms() = liveData {
        alarmRepository.alarms.collect{
            emit(it)
        }
    }
}