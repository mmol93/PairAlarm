package com.easyo.pairalarm.ui.activity

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.easyo.pairalarm.R
import com.easyo.pairalarm.broadcast.AlarmReceiver
import com.easyo.pairalarm.databinding.ActivityMainBinding
import com.easyo.pairalarm.service.AlarmForeground
import com.easyo.pairalarm.ui.fragment.AlarmFragment
import com.easyo.pairalarm.ui.fragment.SettingFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewPager.isUserInputEnabled = false

        val receiver = ComponentName(this, AlarmReceiver::class.java)

        packageManager.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )

        // TODO: 나중에 추가할 내용
        val alarmFragment = AlarmFragment()
//        val communityFragment = CommunityFragment()
//        val weatherFragment = WeatherFragment()
        val settingFragment = SettingFragment()

        val fragments = arrayListOf(alarmFragment, settingFragment)

        // viewPager2에서 사용할 adapter
        val viewPagerAdapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int {
                return fragments.size
            }

            override fun createFragment(position: Int): Fragment {
                return fragments[position]
            }
        }

        // viewPager에 adapter 등록
        binding.viewPager.run {
            adapter = viewPagerAdapter
            // viewPager2의 페이지를 초기화 하지 않게 한다
            offscreenPageLimit = fragments.size
        }

        // tab과 viewPager2를 연결시킨다
        TabLayoutMediator(binding.tabs, binding.viewPager) { tab: TabLayout.Tab, i: Int ->
            when (i) {
                0 -> {
                    tab.icon = getDrawable(R.drawable.ic_clock)
                }
                1 -> {
                    tab.icon = getDrawable(R.drawable.ic_baseline_setting)
                }
            }
        }.attach()
    }
}
