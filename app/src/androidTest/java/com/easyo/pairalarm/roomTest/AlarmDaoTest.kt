package com.easyo.pairalarm.roomTest

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.easyo.pairalarm.database.AppDatabase
import com.easyo.pairalarm.database.dao.AlarmDAO
import com.easyo.pairalarm.database.table.AlarmData
import com.easyo.pairalarm.utils.initCurrentAlarmDataForTest
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
@ExperimentalCoroutinesApi
class AlarmDaoTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: AppDatabase
    private lateinit var dao: AlarmDAO

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), AppDatabase::class.java
        )
            .allowMainThreadQueries() //this is a test case, we don't want other thread pools
            .build()

        dao = database.alarmDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    // insert는 suspend 함수이기 때문에 runTest 안에서 실시한다
    fun insertAlarmDataTest() = runTest {
        val exampleAlarmData = initCurrentAlarmDataForTest()
        dao.insertNewAlarm(exampleAlarmData)

        // ArtsData를 DB에서 가져온다
        val artDataList = dao.getAllAlarms().first()

        // 가져온 데이터에 exampleArt이 있는지 확인한다
        assertThat(artDataList).contains(exampleAlarmData)
    }

    @Test
    fun deleteAlarmDataTest() = runTest {
        val exampleAlarmData = initCurrentAlarmDataForTest()
        dao.insertNewAlarm(exampleAlarmData)
        dao.deleteAlarm(exampleAlarmData)

        val result = dao.getAllAlarms().firstOrNull()
        assertThat(result).isEqualTo(emptyList<AlarmData>())
    }
}