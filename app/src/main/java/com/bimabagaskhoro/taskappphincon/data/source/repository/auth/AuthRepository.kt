package com.bimabagaskhoro.taskappphincon.data.source.repository.auth

import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangeImage
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangePassword
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseLogin
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseRegister
import com.bimabagaskhoro.taskappphincon.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRepository {
    fun login(
        email: String,
        password: String
    ): Flow<Resource<ResponseLogin>>

    fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int
    ): Flow<Resource<ResponseRegister>>

    fun changePassword(
        //token: String,
        id: Int,
        password: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Resource<ResponseChangePassword>>

    fun changeImage(
        //token: String,
        id: Int,
        image: MultipartBody.Part
    ): Flow<Resource<ResponseChangeImage>>
}