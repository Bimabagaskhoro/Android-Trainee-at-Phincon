package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DataStoreViewModel @Inject constructor(
    private val dataPreference: AuthPreferences
) : ViewModel() {
    fun isLogin(state: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveIsLoggedIn(state) }
    }

    fun saveToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveUserToken(token) }
    }

    fun saveRefreshToken(token: String) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveRefreshToken(token) }
    }

    fun saveUserId(userId: Int) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveUserId(userId) }
    }

    fun saveUserName(username: String) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveUserName(username) }
    }

    fun saveUserEmail(email: String) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveUserEmail(email) }
    }

    fun saveUserGender(gender: Int) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveGender(gender) }
    }

    fun saveUserPhone(phone: String) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveUserPhone(phone) }
    }

    fun saveUserPath(path: String) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveUserPath(path) }
    }

    fun saveUserPhoto(path: String) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveUserImage(path) }
    }

    fun saveLanguage(language: String) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveUserLanguage(language) }
    }

    fun clear() {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.clear() }
    }

    fun saveUserLanguage(isChecked: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveTrolleyChecked(isChecked) }
    }

    fun saveIsRead(isRead: Boolean) {
        viewModelScope.launch(Dispatchers.IO) { dataPreference.saveIsRead(isRead) }
    }

    val isLoggedIn = dataPreference.isLoggedIn.asLiveData()
    val getToken = dataPreference.userToken
    val getRefreshToken = dataPreference.userRefreshToken
    val getUserId = dataPreference.userId
    val getUserName = dataPreference.userName
    val getUserEmail = dataPreference.userEmail
    val getUserGender = dataPreference.userGender
    val getUserPhone = dataPreference.userPhone
    val getUserPath = dataPreference.userPath
    val getUserImage = dataPreference.userImage
    val getUserLanguage = dataPreference.userLanguage
    val getUserChecked = dataPreference.trolleyChecked
    val getUserIsRead = dataPreference.isRead

}