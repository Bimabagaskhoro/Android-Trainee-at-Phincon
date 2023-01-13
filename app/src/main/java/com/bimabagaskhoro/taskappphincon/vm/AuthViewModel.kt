package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bimabagaskhoro.taskappphincon.data.source.Resource
import com.bimabagaskhoro.taskappphincon.data.source.repository.AuthRepository
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseChangeImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository) : ViewModel(){
    fun login(email: String, password: String) = authRepository.login(email, password).asLiveData()

    fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int,
    ): LiveData<Resource<ResponseRegister>> =
        authRepository.register(image, email, password, name, phone, gender).asLiveData()

    fun changePassword(id:Int, oldPassword: String, newPassword: String, confirmPassword: String) = authRepository.changePassword(id,oldPassword, newPassword,confirmPassword).asLiveData()

    fun changeImage(
        id: Int,
        image: MultipartBody.Part,
    ): LiveData<Resource<ResponseChangeImage>> =
        authRepository.changeImage(id, image).asLiveData()

    fun refreshToken(idUser: Int, accessToken: String, refreshToken: String) = authRepository.refreshToken(idUser, accessToken, refreshToken).asLiveData()
}