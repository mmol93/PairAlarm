package com.easyo.pairalarm.worker

import android.content.Context
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.easyo.pairalarm.AppClass.Companion.dataStore
import com.easyo.pairalarm.R
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.model.*
import com.easyo.pairalarm.repository.AlarmRepository
import com.easyo.pairalarm.util.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import timber.log.Timber

/**
 * 새롭게 알람을 추가하고 DB에서 다음 알림을 찾고 바로 등록하거나
 * 그냥 DB에서 다음 알림을 찾고 바로 등록한다
 * */
@HiltWorker
class NextAlarmWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val alarmRepository: AlarmRepository,
) : CoroutineWorker(appContext, workerParams) {
    private var quickAlarmBellIndex: Int = 0
    private var quickAlarmModeIndex: Int = 0
    private var quickAlarmVibrationIndex: Int = 0
    override suspend fun doWork(): Result {
        // Noti에 있는 액션 버튼 클릭으로 호출된건지 확인
        val actionButtonPosition = inputData.getInt(ACTION_BUTTON, 0)

        if (actionButtonPosition != 0) {
            applicationContext.dataStore.data.catch { exception ->
                Timber.e(exception.message)
                showShortToast(
                    applicationContext,
                    applicationContext.getString(R.string.toast_get_dataStore_error)
                )
            }.first().let {
                quickAlarmBellIndex =
                    it[stringPreferencesKey(SettingContents.QUICKALARM_BELL.title)]?.getBellIndex()
                        ?: BellType.WALKING.title.getBellIndex()
                quickAlarmModeIndex =
                    it[stringPreferencesKey(SettingContents.QUICKALARM_MODE.title)]?.getModeIndex()
                        ?: AlarmMode.NORMAL.mode.getModeIndex()
                quickAlarmVibrationIndex =
                    it[stringPreferencesKey(SettingContents.QUICKALARM_MUTE.title)]?.getVibrationIndex()
                        ?: AlarmVibrationOption.SOUND.vibrationOptionName.getVibrationIndex()
            }

            when (actionButtonPosition) {
                NOTI_ACTION1_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION1_REQUEST_CODE)
                }
                NOTI_ACTION2_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION2_REQUEST_CODE)
                }
                NOTI_ACTION3_REQUEST_CODE -> {
                    makeQuickAlarm(NOTI_ACTION3_REQUEST_CODE)
                }
            }
            resetAlarmNotification()
        } else {
            resetAlarmNotification()
        }
        return Result.success()
    }

    private suspend fun makeQuickAlarm(notiActionRequestCode: Int) {
        makeAlarmDataForQuickAlarm(notiActionRequestCode)?.let {
            Timber.d("makeQuickAlarm called")
            alarmRepository.insertAlarmData(it)
        }
    }

    private fun makeAlarmDataForQuickAlarm(notiActionRequestCode: Int): AlarmData? {
        return when (notiActionRequestCode) {
            NOTI_ACTION1_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(
                    5 * 60 * 1000,
                    quickAlarmBellIndex,
                    quickAlarmModeIndex,
                    quickAlarmVibrationIndex
                )
            }
            NOTI_ACTION2_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(
                    15 * 60 * 1000,
                    quickAlarmBellIndex,
                    quickAlarmModeIndex,
                    quickAlarmVibrationIndex
                )
            }
            NOTI_ACTION3_REQUEST_CODE -> {
                getAlarmDataFromTimeMillis(
                    30 * 60 * 1000,
                    quickAlarmBellIndex,
                    quickAlarmModeIndex,
                    quickAlarmVibrationIndex
                )
            }
            else -> null
        }
    }

    private suspend fun resetAlarmNotification() {
        alarmRepository.getAllAlarm().first().let { alarmDataList ->
            val nextAlarmText = getNextAlarm(alarmDataList)
            if (nextAlarmText.isNullOrEmpty()) {
                cancelAlarmNotification(applicationContext)
            } else {
                Timber.d("resetAlarmNotification called")
                makeAlarmNotification(applicationContext, nextAlarmText.toString())
            }
            // 모든 알람의 브로드캐스트를 새롭게 지정
            resetAllAlarms(applicationContext, alarmDataList)
        }
    }
}
