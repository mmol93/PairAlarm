package com.easyo.pairalarm.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.animation.Animator
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.easyo.pairalarm.R
import com.easyo.pairalarm.databinding.ActivityInitialBinding
import com.easyo.pairalarm.eventbus.EventBus
import com.easyo.pairalarm.eventbus.InitDataEvent
import com.easyo.pairalarm.util.IS_INIT_APP
import com.easyo.pairalarm.util.MyTimber
import com.easyo.pairalarm.worker.InitAlarmDataWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInitialBinding
    private var isDoneLottieAnimation = false
    private var isDoneDatabaseLoad = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInitialBinding.inflate(layoutInflater)
        setContentView(binding.root)
        Timber.plant(MyTimber())

        // Lottie 애니메이션이 끝났을 때 MainActivity 열기
        binding.lottieView.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                isDoneLottieAnimation = true
                // 애니메이션이 끝나고 DB를 다 불러 왔을 때만 MainActivity을 연다
                if (isDoneDatabaseLoad){
                    openMainActivity()
                }
            }

            override fun onAnimationCancel(animation: Animator) {
            }

            override fun onAnimationRepeat(animation: Animator) {

            }
        })

        // InitDataEvent를 subscribe하여 EventBus의 post의 신호를 감지한다
        lifecycleScope.launch {
            EventBus.subscribe<InitDataEvent>().first {
                Timber.d("EventBus collected")
                updateProgress(it)
                true
            }
        }

        // 현재 화면에서 해당 Worker를 사용하게 한다
        val initWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<InitAlarmDataWorker>().build()
        WorkManager.getInstance(applicationContext).enqueue(initWorkRequest as OneTimeWorkRequest)
    }

    // MainActivity 열기
    private fun openMainActivity(){
        val mainActivityIntent = Intent(this, MainActivity::class.java)
        mainActivityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        mainActivityIntent.putExtra(IS_INIT_APP, true)
        startActivity(mainActivityIntent)
        finish()
    }

    // UI에 있는 Progress 상태 업데이트 하기
    private fun updateProgress(initDataEvent: InitDataEvent) {
        binding.progressHorizontal.setProgressCompat(initDataEvent.progress, true)
        binding.progressText.text = initDataEvent.message

        if (initDataEvent.progress == Int.MAX_VALUE) {
            isDoneDatabaseLoad = true
            binding.progressText.text = getString(R.string.progressFinish)
            if (isDoneDatabaseLoad && isDoneLottieAnimation){
                // animation이 너무 빨리 끝날 수 있으니 1초 대기한 후 다음 화면으로 이동
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    openMainActivity()
                }, 500)
            }
        }
    }
}
