package com.easyo.pairalarm.util

import android.media.MediaPlayer
import kotlinx.coroutines.flow.MutableStateFlow

class AlarmBell{
    companion object{
        private var bellIndex = 0

        fun getBellIndex() = bellIndex

        fun setBellIndex(newValue: Int) {
            bellIndex = newValue
        }
    }
}

class AlarmMode{
    companion object{
        private var modeIndex = 0

        fun getModeIndex() = modeIndex

        fun setModeIndex(newValue: Int) {
            modeIndex = newValue
        }
    }
}

class AlarmMusic{
    companion object{
        private var musicIndex: MediaPlayer? = null

        fun getCurrentMusic() = musicIndex

        fun setCurrentMusic(selectedMusic: MediaPlayer?){
            musicIndex = selectedMusic
        }
    }
}