package com.easyo.pairalarm.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.easyo.pairalarm.databinding.FragmentUserGuideBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UserGuideFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentUserGuideBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
    }
}