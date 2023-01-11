package com.bimabagaskhoro.taskappphincon.data.source.repository

import com.bimabagaskhoro.taskappphincon.data.source.Resource
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseLogin
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.response.SuccessResponse
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Part

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
}