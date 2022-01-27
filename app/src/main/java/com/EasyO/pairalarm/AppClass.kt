package com.EasyO.pairalarm

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.EasyO.pairalarm.database.table.AlarmData
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
        var alarmData: Flow<List<AlarmData>>? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}