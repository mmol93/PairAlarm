package com.easyo.pairalarm.di

import android.content.Context
import androidx.room.Room
import com.easyo.pairalarm.database.AppDatabase
import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.repository.AlarmRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class AppModule {
    @Provides
    fun provideContext(@ApplicationContext context: Context): Context {
        return context
    }
    @Provides
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "alarm_data_database").build()
    }

    @Provides
    @Singleton
    fun provideAlarmDao(appDatabase: AppDatabase): AlarmDAO {
        return appDatabase.alarmDao()
    }

    @Provides
    @Singleton
    fun provideAlarmRepository(alarmDAO: AlarmDAO):AlarmRepository{
        return AlarmRepository(alarmDAO)
    }
}