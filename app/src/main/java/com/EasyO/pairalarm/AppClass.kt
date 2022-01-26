package com.EasyO.pairalarm

import android.app.Application
import com.EasyO.pairalarm.database.table.AlarmData
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.flow.Flow

@HiltAndroidApp
class AppClass:Application() {
    companion object{
        lateinit var context : AppClass
        var alarmData: Flow<List<AlarmData>>? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}