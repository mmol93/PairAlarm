package com.easyo.pairalarm

sealed class ApiResponse<out T> {
    data class Success<T>(val value: T) : ApiResponse<T>()
    object Loading : ApiResponse<Nothing>()
    data class Failure(val e: Throwable) : ApiResponse<Nothing>()
}