package com.easyo.pairalarm.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.easyo.pairalarm.databinding.ActivityUserGuideBinding

class UserGuideActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserGuideBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserGuideBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}