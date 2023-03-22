package com.easyo.pairalarm.broadcastTest

import android.content.Intent
import android.content.IntentFilter
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.easyo.pairalarm.broadcast.AlarmReceiver
import junit.framework.TestCase.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

// adb로는 다음 커맨드로 테스트 가능
// adb shell am broadcast -n com.easyo.pairalarm/com.easyo.pairalarm.broadcast.AlarmReceiver -e action_button "action1"
@RunWith(AndroidJUnit4::class)
class AlarmBroadCastTest {
    private val context = InstrumentationRegistry.getInstrumentation().context
    private lateinit var receiver: AlarmReceiver

    @Before
    fun setup() {
        receiver = AlarmReceiver()
        val intentFilter = IntentFilter().apply {
            addAction(context.packageName)
        }
        InstrumentationRegistry.getInstrumentation().targetContext.registerReceiver(
            receiver,
            intentFilter
        )
    }

    @Test
    fun test_MY_PACKAGE_REPLACED_OnReceive() {
        val intent = Intent(context.packageName)
        context.sendOrderedBroadcast(intent, null)
        // wait for onReceive() in Receive class
        Thread.sleep(2000)
        assertTrue(receiver.broadcastCalled)
    }

    @After
    fun tearDown() {
        InstrumentationRegistry.getInstrumentation().targetContext.unregisterReceiver(receiver)
    }
}