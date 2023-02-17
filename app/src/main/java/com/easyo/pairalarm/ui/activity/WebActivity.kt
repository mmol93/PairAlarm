package com.easyo.pairalarm.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityWebBinding

class WebActivity : AppCompatActivity() {
    private lateinit var binding: ActivityWebBinding
    private lateinit var title: String
    private lateinit var url: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_web)
        title = intent?.getStringExtra("title") ?: ""
        url = intent?.getStringExtra("url") ?: ""
        binding.webView.run {
            webViewClient = this.CommonWebViewClient()
            loadUrl(this@WebActivity.url)
        }
    }
}