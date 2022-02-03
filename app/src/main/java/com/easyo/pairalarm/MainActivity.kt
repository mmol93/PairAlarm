package com.easyo.pairalarm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.easyo.pairalarm.databinding.ActivityMainBinding
import com.easyo.pairalarm.ui.fragment.AlarmFragment
import com.easyo.pairalarm.ui.fragment.CommunityFragment
import com.easyo.pairalarm.ui.fragment.SettingFragment
import com.easyo.pairalarm.ui.fragment.WeatherFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val alarmFragment = AlarmFragment()
        val communityFragment = CommunityFragment()
        val weatherFragment = WeatherFragment()
        val settingFragment = SettingFragment()

        val fragments = arrayListOf(alarmFragment, communityFragment, weatherFragment, settingFragment)

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
            if (i == 0) {
                tab.icon = getDrawable(R.drawable.ic_baseline_alarm)
            } else if (i == 1) {
                tab.icon = getDrawable(R.drawable.ic_baseline_people)
            } else if (i == 2) {
                tab.icon = getDrawable(R.drawable.ic_baseline_weather)
            } else if (i == 3) {
                tab.icon = getDrawable(R.drawable.ic_baseline_setting)
            }
        }.attach()

    }
}