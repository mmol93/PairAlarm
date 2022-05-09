package com.easyo.pairalarm.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.easyo.pairalarm.AppClass.Companion.requestCode
import com.easyo.pairalarm.databinding.ActivityOnAlarmBinding
import com.easyo.pairalarm.viewModel.AlarmViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OnAlarmActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOnAlarmBinding
    private val alarmViewModel: AlarmViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (requestCode != null) {
            val goesOnAlarmData = alarmViewModel.searchRequestCode(requestCode.toString())
            lifecycleScope.launch {
                goesOnAlarmData.collectLatest {alarmDataList ->

                }
            }
        }
    }
}