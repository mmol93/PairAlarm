package com.easyo.pairalarm.dataStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.easyo.pairalarm.AppClass.Companion.dataStore
import com.easyo.pairalarm.R
import com.easyo.pairalarm.util.makeToast
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber

interface DataStoreTool {

    val context: Context

    fun getStoredDataWithFlow(key: String): Flow<String> {
        val setDataKey = stringPreferencesKey(key)
        return context.dataStore.data.catch { exception ->
            Timber.d(exception.message)
            makeToast(context, context.getString(R.string.toast_get_dataStore_error))
        }.map { preferences ->
            // No type safety.
            preferences[setDataKey] ?: ""
        }
    }

    suspend fun saveStringData(key: String, textData: String) {
        val setDataKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[setDataKey] = textData
        }
    }
}
