package com.easyo.pairalarm.viewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.repository.AlarmRepository
import com.easyo.pairalarm.util.cancelAlarm
import com.easyo.pairalarm.util.getCurrentHour
import com.easyo.pairalarm.util.getCurrentMinute
import com.easyo.pairalarm.util.setAlarm
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val alarmRepository: AlarmRepository) : ViewModel() {
    val currentAlarmHour = MutableStateFlow(0)
    val currentAlarmMin = MutableStateFlow(0)
    val currentAlarmBell = MutableStateFlow(0)
    val currentAlarmMode = MutableStateFlow(0)

    fun getAllAlarmData() = alarmRepository.getAllAlarm()

    fun insertAlarmData(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.insertAlarmData(alarmData)
        Timber.d("inserted: $alarmData")
    }

    fun updateAlarmData(context: Context, alarmData: AlarmData) = viewModelScope.launch {
        // TODO: 이거 처리 로딩중인거 만들어야할듯
        alarmRepository.updateAlarmData(alarmData)
        if (alarmData.button){
            cancelAlarm(context, alarmData.alarmCode)
            setAlarm(context, alarmData.alarmCode.toInt(), alarmData.hour, alarmData.minute)
        }else{
            cancelAlarm(context, alarmData.alarmCode)
        }
    }

    fun deleteAlarmData(alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.deleteAlarmData(alarmData)
    }

    fun searchAlarmCode(alarmCode: String) = alarmRepository.searchWithAlarmCode(alarmCode)

    fun getAlarmData(alarmCode: String?): Flow<AlarmData> {
        return if (alarmCode != null) {
            alarmRepository.searchWithAlarmCode(alarmCode)
        } else {
            initCurrentAlarmDataFlow()
        }
    }

    private fun initCurrentAlarmDataFlow(): Flow<AlarmData> {
        return flowOf(
            initCurrentAlarmData()
        )
    }

    private fun initCurrentAlarmData(): AlarmData {
        return AlarmData(
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
            alarmCode = "",
            mode = 0,
            hour = getCurrentHour(),
            minute = getCurrentMinute(),
            quick = false,
            volume = 100,
            bell = 0,
            name = ""
        )
    }
}
