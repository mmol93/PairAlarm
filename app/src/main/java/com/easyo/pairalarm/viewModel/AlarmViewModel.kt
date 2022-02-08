package com.easyo.pairalarm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val alarmRepository: AlarmRepository): ViewModel() {
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