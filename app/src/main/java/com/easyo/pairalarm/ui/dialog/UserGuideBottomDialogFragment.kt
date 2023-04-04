package com.easyo.pairalarm.ui.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import autoCleared
import com.easyo.pairalarm.databinding.FragmentUserGuideBinding
import com.easyo.pairalarm.model.RecyclerItemContentsType
import com.easyo.pairalarm.model.UserGuideContents
import com.easyo.pairalarm.recyclerItem.SpacerItem
import com.easyo.pairalarm.recyclerItem.UserGuideItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.xwray.groupie.GroupieAdapter

class UserGuideBottomDialogFragment : BottomSheetDialogFragment() {
    private var binding: FragmentUserGuideBinding by autoCleared()
    private val userGuideRecyclerAdapter = GroupieAdapter()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState) as BottomSheetDialog
        dialog.setOnShowListener { setupBottomSheet(it) }
        return dialog
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val settingItemList = UserGuideContents.values().map { it }

        binding.userGuideRecycler.apply {
            adapter = userGuideRecyclerAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        setupUserGuideList(settingItemList)
    }

    private fun setupUserGuideList(userGuideItemList: List<UserGuideContents>) {
        when {
            userGuideItemList.size == 1 -> {
                userGuideItemList.map { userGuideItem ->
                    UserGuideItem(
                        requireContext(),
                        RecyclerItemContentsType.SINGLE,
                        userGuideItem
                    ).also { userGuideRecyclerAdapter.add(it) }
                }
            }
            userGuideItemList.size > 1 -> {
                userGuideItemList.mapIndexed { index, data ->
                    when (index) {
                        // 첫 번째 항목이거나
                        0 -> {
                            if (userGuideItemList.size == 2) {
                                setupItem(data, RecyclerItemContentsType.FIRST)
                            } else {
                                setupItem(data, RecyclerItemContentsType.FIRST)
                            }
                        }

                        // 마지막 항목일 경우
                        userGuideItemList.size - 1 -> {
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

    private fun setupItem(
        data: UserGuideContents? = null,
        contentType: RecyclerItemContentsType? = null
    ) {
        if (data == null) {
            SpacerItem.xnormal().also { userGuideRecyclerAdapter.add(it) }
        } else {
            UserGuideItem(
                requireContext(),
                recyclerItemContentsType = contentType,
                userGuideContents = data
            ).also { userGuideRecyclerAdapter.add(it) }
        }
    }

    private fun setupBottomSheet(dialogInterface: DialogInterface) {
        val bottomSheetDialog = dialogInterface as BottomSheetDialog
        val bottomSheet = bottomSheetDialog.findViewById<View>(
            com.google.android.material.R.id.design_bottom_sheet)
            ?: return
        bottomSheet.setBackgroundColor(Color.TRANSPARENT)
    }
}