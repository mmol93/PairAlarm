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
    lateinit var context : AppClass

    companion object{
        var requestCode:String? = null
    }

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}