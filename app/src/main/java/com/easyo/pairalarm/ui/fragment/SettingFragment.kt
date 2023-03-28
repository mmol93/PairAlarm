package com.easyo.pairalarm.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import autoCleared
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentSettingBinding
import com.easyo.pairalarm.model.RecyclerItemContentsType
import com.easyo.pairalarm.model.SettingContents
import com.easyo.pairalarm.recyclerItem.SettingContentItem
import com.easyo.pairalarm.recyclerItem.SpacerItem
import com.easyo.pairalarm.ui.dialog.UserGuideFragment
import com.xwray.groupie.GroupieAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private var binding: FragmentSettingBinding by autoCleared()
    private val settingRecyclerAdapter = GroupieAdapter()
    private val job by lazy { Job() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentSettingBinding>(view)?.let { binding = it } ?: return

        binding.lifecycleOwner = this
        val settingItemList = SettingContents.values().map { it }

        binding.settingRecycler.apply {
            adapter = settingRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        setupSettingData(settingItemList)
    }

    private fun setupSettingData(settingItemList: List<SettingContents>) {
        when {
            settingItemList.size == 1 -> {
                settingItemList.map { settingItem ->
                    SettingContentItem(
                        requireContext(),
                        settingItem,
                        RecyclerItemContentsType.SINGLE,
                        Dispatchers.Main,
                        job
                    ) { showUserGuideFragment() }.also { settingRecyclerAdapter.add(it) }
                }
            }
            settingItemList.size > 1 -> {
                settingItemList.mapIndexed { index, data ->
                    if (data == SettingContents.BLANK) {
                        setupItem()
                    } else {
                        when {
                            // 첫 번째 항목이거나 직전 값이 빈칸일 경우
                            index == 0 || (settingItemList[index - 1] == SettingContents.BLANK) -> {
                                if (settingItemList.size == 2) {
                                    setupItem(data, RecyclerItemContentsType.FIRST)
                                } else {
                                    setupItem(data, RecyclerItemContentsType.FIRST)
                                }
                            }
                            // 마지막 항목이거나 이후 값이 빈칸일 경우
                            index == settingItemList.size - 1 || (settingItemList[index + 1] == SettingContents.BLANK) -> {
                                setupItem(data, RecyclerItemContentsType.LAST)
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

    private fun showUserGuideFragment() {
        val bottomSheet = UserGuideFragment()
        bottomSheet.show(requireActivity().supportFragmentManager, bottomSheet.tag)
    }

    private fun setupItem(
        data: SettingContents? = null,
        contentType: RecyclerItemContentsType? = null
    ) {
        if (data == null) {
            SpacerItem.xnormal().also { settingRecyclerAdapter.add(it) }
        } else {
            SettingContentItem(
                requireContext(),
                data,
                contentType,
                Dispatchers.Main,
                job
            ) { showUserGuideFragment() }
                .also { settingRecyclerAdapter.add(it) }
        }
    }
}
