package com.easyo.pairalarm.util

import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.databinding.BindingAdapter
import com.easyo.pairalarm.R
import com.google.android.material.button.MaterialButton

object BindingAdapter {
    /**
     *  시간을 NumberPicker에 세팅한다
     *
     *  @param hour 시간(24시간 포맷)
     */
    @JvmStatic
    @BindingAdapter("hour")
    fun NumberPicker.setHour(hour: Int) {
        this.value = if (hour > 12) {
            hour - 12
        } else {
            hour
        }
    }

    /**
     *  오전/오후를 NumberPicker에 세팅한다
     *
     *  @param hourForAMPM 시간(24시간 포맷)
     */
    @JvmStatic
    @BindingAdapter("hourForAMPM")
    fun NumberPicker.setAMPM(hourForAMPM: Int) {
        this.value = if (hourForAMPM > 12) {
            1
        } else {
            0
        }
    }

    /**
     *  볼륨에 따라 이미지를 셋팅한다
     *
     *  @param volume 시간(24시간 포맷)
     */
    @JvmStatic
    @BindingAdapter("volume")
    fun ImageView.setVolumeImage(volume: Int) {
        if (volume == 0) {
            setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.volume_mute
                )
            )
        } else {
            setImageDrawable(
                AppCompatResources.getDrawable(
                    context,
                    R.drawable.volume_icon
                )
            )
        }
    }

    /**
     *  벨 인덱스에 따른 텍스트를 붙인다
     *
     *  @param bellIndex 벨 인덱스
     */
    @JvmStatic
    @BindingAdapter("bellIndex")
    fun TextView.setBellText(bellIndex: Int) {
        this.text = when (bellIndex) {
            0 -> context.getString(R.string.bellType_Normal_Walking)
            1 -> context.getString(R.string.bellType_Normal_PianoMan)
            2 -> context.getString(R.string.bellType_Normal_Happy)
            3 -> context.getString(R.string.bellType_Normal_Lonely)
            else -> context.getString(R.string.bellType_Normal_Walking)
        }
    }

    /**
     *  알람 모드에 따른 텍스트를 붙인다
     *
     *  @param alarmMode 알람 인덱스
     */
    @JvmStatic
    @BindingAdapter("alarmMode")
    fun TextView.setAlarmMode(alarmMode: Int) {
        this.text = when (alarmMode) {
            0 -> context.getString(R.string.alarmSet_alarmModeItem1)
            1 -> context.getString(R.string.alarmSet_alarmModeItem2)
            else -> context.getString(R.string.alarmSet_alarmModeItem1)
        }
    }

    @JvmStatic
    @BindingAdapter("week", "weekClicked")
    fun MaterialButton.setColorStroke(week: String?, weekClicked: Boolean) {
        when (week) {
            null -> {
                if (weekClicked) {
                    this.setStrokeColorResource(R.color.deep_yellow)
                } else {
                    this.setStrokeColorResource(R.color.background)
                }
            }
            "Sat" -> {
                if (weekClicked) {
                    setStrokeColorResource(R.color.light_blue)
                } else {
                    setStrokeColorResource(R.color.background)
                }
            }
            "Sun" -> {
                if (weekClicked) {
                    setStrokeColorResource(R.color.red)
                } else {
                    setStrokeColorResource(R.color.background)
                }
            }
        }
    }
}