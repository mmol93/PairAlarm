package com.easyo.pairalarm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val alarmRepository: AlarmRepository) :
    ViewModel() {
    fun getAllAlarmData() = alarmRepository.getAllAlarm()

    fun insertAlarmData(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.insertAlarmData(alarmData)
        Log.d("AlarmViewModel", "inserted: $alarmData")
    }

    fun updateAlarData(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.updateAlarmData(alarmData)
    }

    fun deleteAlarmData(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.deleteAlarmData(alarmData)
    }

    fun searchRequestCode(requestCode: String) = alarmRepository.searchWithAlarmCode(requestCode)
}
