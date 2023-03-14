package com.easyo.pairalarm.broadcastTest

import android.content.Intent
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.easyo.pairalarm.broadcast.AlarmReceiver
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlarmBroadCastTest {
    private val context = InstrumentationRegistry.getInstrumentation().context
    private lateinit var receiver: AlarmReceiver

    @Before
    fun setup() {
        receiver = AlarmReceiver()
    }

    @Test
    fun test_MY_PACKAGE_REPLACED_OnReceive() {
        val intent = Intent("android.intent.action.MY_PACKAGE_REPLACED")
        receiver.onReceive(context, intent)
        assertEquals("android.intent.action.MY_PACKAGE_REPLACED", intent.action)
    }

    @Test
    fun test_BOOT_COMPLETED_OnReceive() {
        val intent = Intent("android.intent.action.BOOT_COMPLETED")
        receiver.onReceive(context, intent)

        assertEquals("android.intent.action.BOOT_COMPLETED", intent.action)
    }
}