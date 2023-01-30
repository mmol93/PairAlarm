package com.easyo.pairalarm.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentSettingBinding
import com.easyo.pairalarm.groupieitem.SettingListAdapter
import com.xwray.groupie.GroupieAdapter

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private lateinit var binding: FragmentSettingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentSettingBinding>(view)?.let { binding = it } ?: return

        val alarmSettingList = resources.getStringArray(R.array.setting_item_alarm_text_array)
        val etcSettingList = resources.getStringArray(R.array.setting_item_ect_text_array)

        val settingRecyclerAdapter = GroupieAdapter()
        binding.settingRecycler.apply {
            adapter = settingRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
        alarmSettingList.map { SettingListAdapter(requireContext(), it) }
            .also { settingRecyclerAdapter.addAll(it) }

        etcSettingList.map { SettingListAdapter(requireContext(), it) }
            .also { settingRecyclerAdapter.addAll(it) }
    }
}