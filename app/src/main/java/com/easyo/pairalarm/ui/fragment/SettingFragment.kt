package com.easyo.pairalarm.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import autoCleared
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentSettingBinding
import com.easyo.pairalarm.groupieitem.SettingContentItem
import com.easyo.pairalarm.groupieitem.SpacerItem
import com.easyo.pairalarm.model.SettingContentType
import com.easyo.pairalarm.model.SettingContents
import com.xwray.groupie.GroupieAdapter

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private var binding: FragmentSettingBinding by autoCleared()
    private val settingRecyclerAdapter = GroupieAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentSettingBinding>(view)?.let { binding = it } ?: return

        val settingItemList = SettingContents.values().map { it.title }

        binding.settingRecycler.apply {
            adapter = settingRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        setupSettingData(settingItemList)
    }

    private fun setupSettingData(settingItemList: List<String>) {
        when {
            settingItemList.size == 1 -> {
                settingItemList.map { settingItem ->
                    SettingContentItem(requireContext(), settingItem, SettingContentType.SINGLE)
                        .also { settingRecyclerAdapter.add(it) }
                }
            }
            settingItemList.size > 1 -> {
                settingItemList.mapIndexed { index, data ->
                    if (data.isEmpty()) {
                        setupItem()
                    } else {
                        when {
                            // 첫 번째 항목이거나 직전 값이 빈칸일 경우
                            index == 0 || (settingItemList[index - 1].isEmpty()) -> {
                                if (settingItemList.size == 2) {
                                    setupItem(data, SettingContentType.FIRST)
                                } else {
                                    setupItem(data, SettingContentType.FIRST)
                                }
                            }
                            // 마지막 항목이거나 이후 값이 빈칸일 경우
                            index == settingItemList.size - 1 || (settingItemList[index + 1].isEmpty()) -> {
                                setupItem(data, SettingContentType.LAST)
                            }
                            else -> {
                                setupItem(data)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setupItem(data: String? = null, contentType: SettingContentType? = null) {
        if (data.isNullOrBlank()) {
            SpacerItem.xnormal().also { settingRecyclerAdapter.add(it) }
        } else {
            SettingContentItem(requireContext(), data, contentType)
                .also { settingRecyclerAdapter.add(it) }
        }
    }
}
