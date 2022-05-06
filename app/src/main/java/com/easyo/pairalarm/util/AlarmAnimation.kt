package com.easyo.pairalarm.util

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.view.View

class AlarmAnimation(){
    lateinit var animatorSet: AnimatorSet

    fun swing(view: View): AnimatorSet{
        animatorSet = AnimatorSet()
        val objectAnimator1 = ObjectAnimator.ofFloat(view, View.ROTATION, 0f, -30f).setDuration(50)
        val objectAnimator2 = ObjectAnimator.ofFloat(view, View.ROTATION, -30f, 30f).setDuration(100)
        val objectAnimator3 = ObjectAnimator.ofFloat(view, View.ROTATION, 30f, -30f).setDuration(100)
        val objectAnimator4 = ObjectAnimator.ofFloat(view, View.ROTATION, -30f, 0f).setDuration(50)

        animatorSet.playSequentially(objectAnimator1, objectAnimator2, objectAnimator3, objectAnimator4)

        return animatorSet
    }

    fun jump(view: View): AnimatorSet{
        animatorSet = AnimatorSet()
        val objectAnimator1 = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, -30f).setDuration(100)
        val objectAnimator2 = ObjectAnimator.ofFloat(view, View.TRANSLATION_Y, 0f, 0f).setDuration(200)

        animatorSet.playSequentially(objectAnimator1, objectAnimator2)

        return animatorSet
    }
}