package com.EasyO.pairalarm

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.animation.Animator
import com.EasyO.pairalarm.databinding.ActivityInitialBinding
import com.EasyO.pairalarm.databinding.ActivityMainBinding
import java.lang.Exception


class InitialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInitialBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mainActivity = Intent(this, MainActivity::class.java)

        // Lottie 애니메이션이 끝났을 때 MainActivity 열기
        binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {

            }

            override fun onAnimationEnd(animation: Animator) {
                mainActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(mainActivity)
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })
    }
}