package com.easyo.pairalarm.viewModel

import android.content.Context
import androidx.lifecycle.viewModelScope
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.repository.AlarmRepository
import com.easyo.pairalarm.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val alarmRepository: AlarmRepository) :
    BaseViewModel() {
    val currentAlarmHour = MutableStateFlow(0)
    val currentAlarmMin = MutableStateFlow(0)
    val currentAlarmBell = MutableStateFlow(0)
    val currentAlarmMode = MutableStateFlow(0)

    fun getAllAlarmData() = alarmRepository.getAllAlarm()

    fun insertAlarmData(context: Context, alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.insertAlarmData(alarmData)
    }.execute(onSuccess = {
        // 브로드캐스트에 알람 예약하기
        setAlarm(context, alarmData.alarmCode.toInt(), alarmData.hour, alarmData.minute)
        }
    )

    fun updateAlarmData(context: Context, alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.updateAlarmData(alarmData)
    }.execute(onSuccess = {
        // 브로드캐스트에 기존 알람 삭제 및 새로운 알람 추가
        if (alarmData.button) {
            cancelAlarm(context, alarmData.alarmCode)
            setAlarm(context, alarmData.alarmCode.toInt(), alarmData.hour, alarmData.minute)
        } else {
            cancelAlarm(context, alarmData.alarmCode)
        }
    }
    )

    fun deleteAlarmData(context: Context, alarmData: AlarmData) = viewModelScope.launch {
        alarmRepository.deleteAlarmData(alarmData)
    }.execute(onSuccess = {
        // 브로드캐스트에 알람 삭제
        cancelAlarm(context, alarmData.alarmCode)
    })

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
}
