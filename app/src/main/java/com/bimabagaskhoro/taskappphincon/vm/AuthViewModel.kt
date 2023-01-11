package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bimabagaskhoro.taskappphincon.data.source.Resource
import com.bimabagaskhoro.taskappphincon.data.source.repository.AuthRepository
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseRegister
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository) : ViewModel(){
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

}