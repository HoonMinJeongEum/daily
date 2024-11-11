package com.example.diaryApp.datastore


import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import javax.inject.Inject

//interface UserStore {
//    fun login(username: String, password: String): Boolean
//}
class UserStore @Inject constructor(private val context: Context) {

    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("User")
        val KEY_ACCESS_TOKEN = stringPreferencesKey("access_token")
        val KEY_REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_PASSWORD = stringPreferencesKey("password")
        val KEY_AUTO_LOGIN_STATE = booleanPreferencesKey("auto_login")



    }

    fun getValue(key: Preferences.Key<String>): Flow<String> {
        return context.dataStore.data.map {
            it[key] ?: ""
        }
            .take(1)
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
    fun getAutoLoginState(): Flow<Boolean> {
        return context.dataStore.data.map {
            it[KEY_AUTO_LOGIN_STATE] ?: false
        }
    }

    suspend fun setAutoLoginState(value: Boolean) {
        context.dataStore.edit {
            it[KEY_AUTO_LOGIN_STATE] = value
        }
    }

    suspend fun clearValue(key: Preferences.Key<String>): UserStore {
        context.dataStore.edit {
            it.remove(key)
        }
        return this
    }
}