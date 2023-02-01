package com.easyo.pairalarm

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AppClass : Application(), Configuration.Provider {
    companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory
    override fun getWorkManagerConfiguration() =
        Configuration.Builder().setWorkerFactory(workerFactory).build()

    lateinit var context: AppClass

    override fun onCreate() {
        super.onCreate()
        context = this
    }
}
