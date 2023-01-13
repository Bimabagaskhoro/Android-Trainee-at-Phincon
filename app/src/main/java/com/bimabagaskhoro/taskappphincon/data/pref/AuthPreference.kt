package com.bimabagaskhoro.taskappphincon.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val ACCESS_TOKEN = stringPreferencesKey("access_token")
private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
private val USER_ID = intPreferencesKey("id")
private val USER_NAME= stringPreferencesKey("name")
private val USER_EMAIL = stringPreferencesKey("email")
private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "application")

@Singleton
class AuthPreference @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    val userToken = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }

    val refreshToken = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = token
        }
    }

    val userId = dataStore.data.map { preferences ->
        preferences[USER_ID] ?: 0
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserId(id: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    val isLoggedIn = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }



    companion object {

    }
}