package com.easyo.pairalarm.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityDebugBinding
import com.easyo.pairalarm.util.SharedPreference
import com.easyo.pairalarm.util.TEST_TEXT_CURRENTTIME

class DebugActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDebugBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_debug)
        val sharedPreference = SharedPreference(this)
        binding.textView.text = sharedPreference.getStringData()
    }
}