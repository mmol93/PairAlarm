package com.easyo.pairalarm.extensions

import com.easyo.pairalarm.model.BellType

fun String.getBellIndex(): Int{
    return when(this){
        BellType.WALKING.title -> 0
        BellType.PINAOMAN.title -> 1
        BellType.HAPPYTOWN.title -> 2
        BellType.LONELY.title -> 3
        else -> 0
    }
}