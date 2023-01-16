package com.bimabagaskhoro.taskappphincon.data.source.repository

import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.network.ApiService
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseLogin
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseChangeImage
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseChangePassword
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseRefreshToken
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
): AuthRepository {

    override fun login(
        email: String,
        password: String
    ): Flow<Resource<ResponseLogin>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.login(email, password)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    401 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
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
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    401 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }

    override fun changePassword(
        token: String,
        id: Int,
        password: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Resource<ResponseChangePassword>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.changePassword(token, id, password, newPassword, confirmPassword)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    401 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }

    override fun changeImage(
        token: String,
        id: Int,
        image: MultipartBody.Part
    ): Flow<Resource<ResponseChangeImage>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.changeImage(token, id, image)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    401 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }

    override fun refreshToken(
        idUser: Int,
        accessToken: String,
        refreshToken: String
    ): Flow<Resource<ResponseRefreshToken>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.refreshToken(idUser, accessToken, refreshToken)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    401 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }
}