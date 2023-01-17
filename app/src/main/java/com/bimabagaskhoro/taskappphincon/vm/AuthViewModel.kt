package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.*
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.repository.auth.AuthRepository
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.*
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
    ) : ViewModel() {

    fun login(
        email: String,
        password: String
    ) = authRepository.login(email, password).asLiveData()

    fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int,
    ): LiveData<Resource<ResponseRegister>> =
        authRepository.register(image, email, password, name, phone, gender).asLiveData()

    fun changePassword(
        //token: String,
        id: Int,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) = authRepository.changePassword(
        //token,
        id, oldPassword, newPassword,confirmPassword).asLiveData()

    fun changeImage(
        //token: String,
        id: Int,
        image: MultipartBody.Part,
    ): LiveData<Resource<ResponseChangeImage>> =
        authRepository.changeImage(
            //token,
            id, image).asLiveData()

}