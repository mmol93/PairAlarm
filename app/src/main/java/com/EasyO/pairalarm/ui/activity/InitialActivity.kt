package com.EasyO.pairalarm.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.animation.Animator
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.EasyO.pairalarm.MainActivity
import com.EasyO.pairalarm.R
import com.EasyO.pairalarm.database.table.AlarmData
import com.EasyO.pairalarm.databinding.ActivityInitialBinding
import com.EasyO.pairalarm.eventbus.EventBus
import com.EasyO.pairalarm.eventbus.InitDataEvent
import com.EasyO.pairalarm.worker.InitAlarmDataWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class InitialActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInitialBinding
    private var lottieAnimationFlag = false
    private var databaseLoadFlag = false
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
                lottieAnimationFlag = true
                // 애니메이션이 끝나고 DB를 다 불러 왔을 때만 MainActivity을 연다
                if (lottieAnimationFlag && databaseLoadFlag){
                    openMainActivity(mainActivity)
                }
            }

            override fun onAnimationCancel(animation: Animator) {

            }

            override fun onAnimationRepeat(p0: Animator?) {

            }
        })

    }

    // MainActivity 열기
    private fun openMainActivity(mainActivity: Intent){
        mainActivity.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(mainActivity)
    }

    override fun onStart() {
        super.onStart()
        // InitDataEvent를 subscribe하여 EventBus를 사용하여 Post로 InitDataEvent
        lifecycleScope.launch {

            EventBus.subscribe<InitDataEvent>().collectLatest {
                Log.d("InitActivity", "EventBus collected")
                updateProgress(it)
            }
        }

        val initChampionWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<InitAlarmDataWorker>().build()
        WorkManager.getInstance(applicationContext)
            .enqueue(initChampionWorkRequest as OneTimeWorkRequest)
    }

    // UI에 있는 Progress 상태 업데이트 하기
    private fun updateProgress(initDataEvent: InitDataEvent) {
        binding.progressHorizontal.setProgressCompat(initDataEvent.progress, true)
        binding.progressText.text = initDataEvent.message

        if (initDataEvent.progress == Int.MAX_VALUE) {
            databaseLoadFlag = true
            binding.progressText.text = getString(R.string.progressFinish)
            if (databaseLoadFlag && lottieAnimationFlag){
                val handler = Handler(Looper.getMainLooper())
                handler.postDelayed({
                    val mainActivity = Intent(baseContext, MainActivity::class.java)
                    openMainActivity(mainActivity)
                }, 1000)
            }
        }
    }
}