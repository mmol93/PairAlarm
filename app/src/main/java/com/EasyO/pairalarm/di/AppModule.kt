package com.EasyO.pairalarm.di

import android.content.Context
import androidx.room.Room
import com.EasyO.pairalarm.database.AppDatabase
import com.EasyO.pairalarm.database.dao.AlarmDAO
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
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "lol-db").build()
    }

    @Provides
    @Singleton
    fun provideChampionDao(appDatabase: AppDatabase): AlarmDAO {
        return appDatabase.alarmDao()
    }
}