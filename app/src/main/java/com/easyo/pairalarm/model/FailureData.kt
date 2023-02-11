package com.easyo.pairalarm.model

data class Failure(
    val error: Throwable,
    val retry: (() -> Unit)? = null
)