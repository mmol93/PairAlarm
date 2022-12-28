package com.easyo.pairalarm.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _failure = MutableLiveData<Failure>()
    val failure: LiveData<Failure> = _failure

    private val _success = MutableLiveData<Boolean>()
    val success: LiveData<Boolean> = _success

    // 주로 묶을 필요 없는 단일 비동기 작업에서 사용
    fun <T> Flow<T>.execute(
        onSuccess: ((T) -> Unit) = {},
        retry: (() -> Unit) = {},
        onCompletion: (() -> Unit) = {}
    ): Job {
        return viewModelScope.launch {
            flowOn(Dispatchers.IO)
                .onStart {
                    _loading.value = true
                }
                .catch {
                    _loading.value = false
                    _failure.value = Failure(it, retry)
                }
                .onCompletion {
                    onCompletion.invoke()
                    Timber.d("complete")
                    _loading.value = false
                    _success.value = true
                }
                .collect {
                    Timber.d("collected")
                    onSuccess(it)
                }
        }
    }

    // start -> collect -> complete
    fun Job.execute(
        onSuccess: (() -> Unit) = {},
        onCompletion: (() -> Unit) = {}
    ) {
        viewModelScope.launch {
            _loading.value = true
            join()
            flowOf(Dispatchers.IO)
                .catch {
                    _loading.value = false
                    _failure.value = Failure(it)
                }
                .onCompletion {
                    onCompletion.invoke()
                    _loading.value = false
                    _success.value = true
                }
                .collect {
                    onSuccess.invoke()
                }
        }
    }

    data class Failure(
        val error: Throwable,
        val retry: (() -> Unit)? = null
    )
}