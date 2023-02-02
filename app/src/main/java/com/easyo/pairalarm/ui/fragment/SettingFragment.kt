package com.easyo.pairalarm.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.easyo.pairalarm.AppClass.Companion.dataStore
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentSettingBinding
import com.easyo.pairalarm.groupieitem.SettingContentItem
import com.easyo.pairalarm.groupieitem.SpacerItem
import com.easyo.pairalarm.model.SettingContentType
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private lateinit var binding: FragmentSettingBinding
    private val settingRecyclerAdapter = GroupieAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentSettingBinding>(view)?.let { binding = it } ?: return

        val alarmSettingList = resources.getStringArray(R.array.setting_item_alarm_text_array)
        val etcSettingList = resources.getStringArray(R.array.setting_item_ect_text_array)

        val EXAMPLE_COUNTER = intPreferencesKey("example_counter")
        val exampleCounterFlow: Flow<Int> = requireContext().dataStore.data
            .map { preferences ->
                // No type safety.
                preferences[EXAMPLE_COUNTER] ?: 0
            }

        binding.settingRecycler.apply {
            adapter = settingRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        setupSettingData(alarmSettingList)
        setupSettingData(emptyArray())
        setupSettingData(etcSettingList)
    }

    private fun setupSettingData(dataList: Array<String>) {
        when {
            dataList.size == 1 -> {
                dataList.map { data ->
                    SettingContentItem(requireContext(), data, SettingContentType.SINGLE)
                        .also { settingRecyclerAdapter.add(it) }
                }
            }
            dataList.size > 1 -> {
                dataList.mapIndexed { index, data ->
                    when(index) {
                        0 -> {
                            if (dataList.size == 2){
                                setupItem(data, SettingContentType.FIRST, true)
                            }else{
                                setupItem(data, SettingContentType.FIRST)
                            }
                        }
                        dataList.size - 1 ->{
                            setupItem(data, SettingContentType.LAST)
                        }
                        else -> {}
                    }
                }
            }
            dataList.isEmpty() -> {
                setupItem()
            }
        }
    }
    private fun setupItem(data: String? = null, contentType: SettingContentType? = null, onlyHasTwoData: Boolean = false) {
        if (data.isNullOrBlank()){
            SpacerItem.xnormal().also { settingRecyclerAdapter.add(it) }
        }else{
            contentType?.let {
                SettingContentItem(requireContext(), data, it, onlyHasTwoData)
                    .also { settingRecyclerAdapter.add(it) }
            }
        }
    }
}