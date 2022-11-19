package com.easyo.pairalarm.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.repository.AlarmRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SimpleAlarmViewModel @Inject constructor(private val alarmRepository: AlarmRepository): ViewModel() {
    val currentAlarmName = MutableStateFlow("")
    val currentAlarmVibration = MutableStateFlow(0)
    val currentAlarmHour = MutableStateFlow(0)
    val currentAlarmMin = MutableStateFlow(0)
    val currentAlarmBell = MutableStateFlow(0)
    val currentAlarmMode = MutableStateFlow(0)
    val currentAlarmVolume = MutableStateFlow(100)

    // SimpleAlarmViewModel에서는 insert만 사용한다
    fun insert(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.insertAlarmData(alarmData)
        Log.d("AlarmViewModel", "inserted: $alarmData")
    }
}
