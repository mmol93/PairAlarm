package com.easyo.pairalarm.extensions

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.easyo.pairalarm.R
import com.easyo.pairalarm.model.Failure
import com.easyo.pairalarm.util.DENIED
import com.easyo.pairalarm.util.EXPLAINED
import com.google.android.material.snackbar.Snackbar
import timber.log.Timber

fun Fragment.getPermissionActivityResultLauncher(
    allGranted: () -> Unit,
    notGranted: () -> Unit
): ActivityResultLauncher<Array<String>> {
    return registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        val deniedPermissionList = permissions.filter { !it.value }.map { it.key }
        when {
            deniedPermissionList.isNotEmpty() -> {
                val map = deniedPermissionList.groupBy { permission ->
                    if (shouldShowRequestPermissionRationale(permission)) DENIED else EXPLAINED
                }
                map[DENIED]?.let {
                    // 단순히 권한이 거부 되었을 때
                }
                map[EXPLAINED]?.let {
                    // 권한 요청이 완전히 막혔을 때(주로 앱 상세 창 열기)
                    notGranted()
                }
            }
            else -> {
                // 모든 권한이 허가 되었을 때
                allGranted()
            }
        }
    }
}

fun Fragment.showErrorSnackBar(error: Failure) {
    view?.let { Snackbar.make(it, R.string.some_error, Snackbar.LENGTH_SHORT) }
        .also { it?.show() }
    Timber.e("error: ${error.error.message}")
}