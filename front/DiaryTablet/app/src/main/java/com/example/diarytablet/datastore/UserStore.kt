package com.example.diarytablet.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

class UserStore @Inject constructor(private val context: Context) { // @Inject 추가
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("User")
        val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_PASSWORD = stringPreferencesKey("password")
        val KEY_PROFILE_IMAGE = stringPreferencesKey("profile_image")
    }

    fun getValue(key: Preferences.Key<String>): Flow<String> {
        return context.dataStore.data.map {
            it[key] ?: ""
        }.take(1)
    }

    suspend fun setValue(
        key: Preferences.Key<String>,
        value: String
    ): UserStore {
        if (value.isNotEmpty()) {
            context.dataStore.edit {
                it[key] = value
            }
        }
        return this
    }

    suspend fun clearValue(key: Preferences.Key<String>): UserStore {
        context.dataStore.edit {
            it.remove(key)
        }
        return this
    }
}
