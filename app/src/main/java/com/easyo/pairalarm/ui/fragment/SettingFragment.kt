package com.easyo.pairalarm.ui.fragment

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.FragmentSettingBinding

class SettingFragment : Fragment(R.layout.fragment_setting) {
    private lateinit var binding: FragmentSettingBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DataBindingUtil.bind<FragmentSettingBinding>(view)?.let { binding = it } ?: return
    }
}