package com.easyo.pairalarm

import android.app.Application
import android.media.MediaPlayer
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.viewModel.AlarmViewModel
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
        var mediaPlayer: MediaPlayer? = null

        // AlarmFragment에서 수정할 때 NormalAlarmActivity에서 같은 viewModel을 사용하기 위해 필요함
        lateinit var alarmViewModel: AlarmViewModel
        private var alarmTimeList = mutableListOf<Long>()
        private var closestAlarm = 0L

        var requestCode:String? = null
    }

    fun getAlarmTimeList(): MutableList<Long> {
        return alarmTimeList
    }

    fun getClosestAlarm() = closestAlarm

    fun setClosestAlarm(timeMillis: Long){
        closestAlarm = timeMillis
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}