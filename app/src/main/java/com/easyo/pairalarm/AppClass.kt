package com.easyo.pairalarm

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.easyo.pairalarm.database.table.AlarmData
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltAndroidApp
class AppClass:Application(), Configuration.Provider {
    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() = Configuration.Builder().setWorkerFactory(workerFactory).build()

    companion object{
        lateinit var context : AppClass
        var alarmDataList: Flow<List<AlarmData>>? = null

        // current가 붙은 것은 모두 alarm setting에서 사용되는 것들임
        var currentAlarmData: AlarmData? = null
        var currentAlarmBell: Int? = null
        var currentAlarmMode: Int = 0   // 0 : 일반 모드 / 1 : 계산 문제 모드
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}