package com.bimabagaskhoro.taskappphincon.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val USER_TOKEN = stringPreferencesKey("user_token")
private val USER_NAME = stringPreferencesKey("user_name")
private val USER_EMAIL = stringPreferencesKey("user_email")
private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

@Singleton
class AuthPreference @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    val userToken = dataStore.data.map { preferences ->
        preferences[USER_TOKEN] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[USER_TOKEN] = token
        }
    }

    val userName = dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    val userEmail = dataStore.data.map { preferences ->
        preferences[USER_EMAIL] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
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

}