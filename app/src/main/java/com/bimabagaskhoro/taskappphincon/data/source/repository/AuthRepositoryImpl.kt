package com.bimabagaskhoro.taskappphincon.data.source.repository

import com.bimabagaskhoro.taskappphincon.data.source.Resource
import com.bimabagaskhoro.taskappphincon.data.source.network.ApiService
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseLogin
import com.bimabagaskhoro.taskappphincon.data.source.response.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.response.SuccessResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : AuthRepository {

    override fun login(
        email: String,
        password: String
    ): Flow<Resource<ResponseLogin>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.login(email, password)
                emit(Resource.Success(response))
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }
    }

    override fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int
    ): Flow<Resource<ResponseRegister>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.register(image, email, password, name, phone, gender)
                emit(Resource.Success(response))
            } catch (e: Exception) {
                emit(Resource.Error(e.message.toString()))
            }
        }
    }
}