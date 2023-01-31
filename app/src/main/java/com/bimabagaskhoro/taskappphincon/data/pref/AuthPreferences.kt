package com.bimabagaskhoro.taskappphincon.data.pref

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.asLiveData
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")

@Singleton
class AuthPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    /**
     * User access token
     */
    val userToken = dataStore.data.map { preferences ->
        preferences[ACCESS_TOKEN] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserToken(token: String) {
        dataStore.edit { preferences ->
            preferences[ACCESS_TOKEN] = token
        }
    }

    /**
     * User refresh token
     */
    val userRefreshToken = dataStore.data.map { preferences ->
        preferences[REFRESH_TOKEN] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveRefreshToken(token: String) {
        dataStore.edit { preferences ->
            preferences[REFRESH_TOKEN] = token
        }
    }

    /**
     * User id
     */
    val userId = dataStore.data.map { preferences ->
        preferences[USER_ID] ?: 0
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserId(id: Int) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = id
        }
    }

    /**
     * User gender
     */
    val userGender = dataStore.data.map { preferences ->
        preferences[USER_GENDER] ?: 0
    }.distinctUntilChanged().asLiveData()

    suspend fun saveGender(id: Int) {
        dataStore.edit { preferences ->
            preferences[USER_GENDER] = id
        }
    }


    /**
     * Is logged in
     */
    val isLoggedIn = dataStore.data.map { preferences ->
        preferences[IS_LOGGED_IN] ?: false
    }

    suspend fun saveIsLoggedIn(isLoggedIn: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
        }
    }

    /**
     * Clear all user data
     */
    suspend fun clear() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    /**
     * User username
     */
    val userName = dataStore.data.map { preferences ->
        preferences[USER_NAME] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserName(name: String) {
        dataStore.edit { preferences ->
            preferences[USER_NAME] = name
        }
    }

    /**
     * User email
     */
    val userEmail = dataStore.data.map { preferences ->
        preferences[USER_EMAIL] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[USER_EMAIL] = email
        }
    }

    /**
     * User phone
     */
    val userPhone = dataStore.data.map { preferences ->
        preferences[USER_PHONE] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserPhone(phone: String) {
        dataStore.edit { preferences ->
            preferences[USER_PHONE] = phone
        }
    }

    /**
     * User phone
     */
    val userPath = dataStore.data.map { preferences ->
        preferences[USER_PATH] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserPath(path: String) {
        dataStore.edit { preferences ->
            preferences[USER_PATH] = path
        }
    }

    /**
     * User phone
     */
    val userImage = dataStore.data.map { preferences ->
        preferences[USER_IMAGE] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserImage(path: String) {
        dataStore.edit { preferences ->
            preferences[USER_IMAGE] = path
        }
    }

    /**
     * isLanguage
     */
    val userLanguage = dataStore.data.map { preferences ->
        preferences[IS_LANGUAGE] ?: ""
    }.distinctUntilChanged().asLiveData()

    suspend fun saveUserLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[IS_LANGUAGE] = language
        }
    }

    /**
     * isChecked
     */
    val trolleyChecked = dataStore.data.map { preferences ->
        preferences[IS_CHECKED] ?: false
    }.distinctUntilChanged().asLiveData()

    suspend fun saveTrolleyChecked(checked: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_CHECKED] = checked
        }
    }



    /**
     * for interceptor
     */

    fun getAccessToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[ACCESS_TOKEN]
        }
    }

    fun getRefreshToken(): Flow<String?> {
        return dataStore.data.map { preferences ->
            preferences[REFRESH_TOKEN]
        }
    }

    fun getUserId(): Flow<Int?> {
        return dataStore.data.map { preferences ->
            preferences[USER_ID]
        }
    }

    companion object {
        private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        private val USER_ID = intPreferencesKey("user_id")
        private val USER_NAME = stringPreferencesKey("user_name")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_GENDER = intPreferencesKey("user_gender")
        private val USER_PHONE = stringPreferencesKey("user_phone")
        private val USER_PATH = stringPreferencesKey("user_path")
        private val USER_IMAGE = stringPreferencesKey("user_image")
        private val IS_LANGUAGE = stringPreferencesKey("is_language")
        private val IS_CHECKED = booleanPreferencesKey("is_checked")
    }

}