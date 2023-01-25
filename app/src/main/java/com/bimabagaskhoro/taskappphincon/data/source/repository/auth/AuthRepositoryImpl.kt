package com.bimabagaskhoro.taskappphincon.data.source.repository.auth

import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiService
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestRating
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestStock
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangeImage
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangePassword
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseLogin
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.ResponseDetail
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseAddFavorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
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
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
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
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }

    override fun changePassword(
        id: Int,
        password: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Resource<ResponseChangePassword>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.changePassword(id, password, newPassword, confirmPassword)
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
        id: Int,
        image: MultipartBody.Part
    ): Flow<Resource<ResponseChangeImage>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.changeImage(id, image)
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

    override fun addFavorite(userId: Int, idProduct: Int): Flow<Resource<ResponseAddFavorite>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.addFavorite(idProduct,userId)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }

    override fun getDetailProduct(idProduct: Int, idUser: Int): Flow<Resource<ResponseDetail>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.getDetail(idProduct, idUser)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }

    override fun updateStock(requestStock: RequestStock): Flow<Resource<ResponseAddFavorite>> {
        return flow {
            try {
                emit(Resource.Loading())
                val response = apiService.updateStock(requestStock)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }

    override fun unFavorite(userId: Int, idProduct: Int): Flow<Resource<ResponseAddFavorite>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.unFavorite(idProduct,userId)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }

    override fun updateRate(userId: Int, rate: RequestRating): Flow<Resource<ResponseAddFavorite>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.updateRating(userId, rate)
                emit(Resource.Success(response))
            } catch (t: HttpException) {
                when (t.code()) {
                    400 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    404 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    500 -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                    else -> emit(Resource.Error(true, t.code(), t.response()?.errorBody()))
                }
            }
        }
    }
}