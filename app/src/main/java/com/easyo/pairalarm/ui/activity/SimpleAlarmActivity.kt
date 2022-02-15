package com.easyo.pairalarm.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivitySimpleAlarmBinding

class SimpleAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySimpleAlarmBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySimpleAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}