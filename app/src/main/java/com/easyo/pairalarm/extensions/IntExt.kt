package com.easyo.pairalarm.extensions

import com.easyo.pairalarm.model.BellType

fun Int.getBellDescription(): String{
    return when(this){
        0 -> BellType.WALKING.title
        1 -> BellType.PINAOMAN.title
        2 -> BellType.HAPPYTOWN.title
        3 -> BellType.LONELY.title
        else -> BellType.WALKING.title
    }
}