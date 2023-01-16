package com.bimabagaskhoro.taskappphincon.data.source.repository

import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.*
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
    ) : Flow<Resource<ResponseRegister>>

    fun changePassword(
        token: String,
        id: Int,
        password: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Resource<ResponseChangePassword>>

    fun changeImage(
        token: String,
        id: Int,
        image: MultipartBody.Part
    ): Flow<Resource<ResponseChangeImage>>
}