package com.bimabagaskhoro.taskappphincon.data.source.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bimabagaskhoro.taskappphincon.data.source.remote.ProductPagingSource
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
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.ResponseProduct
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : AuthRepository {
    override fun getDataProduct(
        search: String?
    ): Flow<PagingData<DataItemProduct>> {
        return Pager(
            config = PagingConfig(
                enablePlaceholders = false,
                pageSize = 5
            ),
            pagingSourceFactory = {
                ProductPagingSource(search, apiService)
            },
        ).flow
    }

    override fun login(
        email: String,
        password: String,
        tokenFcm: String
    ): Flow<Resource<ResponseLogin>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.login(email, password, tokenFcm)
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
                val response = apiService.addFavorite(idProduct, userId)
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

    override fun updateStock(
        data: RequestStock
    ): Flow<Resource<ResponseAddFavorite>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.updateStock(
//                    dataStock,
                    data
                )
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
                val response = apiService.unFavorite(idProduct, userId)
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

    override fun getDataFavProduct(
        userId: Int,
        search: String?
    ): Flow<Resource<ResponseFavorite>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.getListFav(userId, search)
                val data = response.success.data
                if (data.isNotEmpty()) {
                    emit(Resource.Success(response))
                } else {
                    emit(Resource.Empty())
                }
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

    override fun getOtherProduct(userId: Int): Flow<Resource<ResponseProduct>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.getOtherProduct(userId)
                val data = response.success.data
                if (data.isNotEmpty()) {
                    emit(Resource.Success(response))
                } else {
                    emit(Resource.Empty())
                }
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

    override fun getHistoryProduct(userId: Int): Flow<Resource<ResponseProduct>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.getHistoryProduct(userId)
                val data = response.success.data
                if (data.isNotEmpty()) {
                    emit(Resource.Success(response))
                } else {
                    emit(Resource.Empty())
                }
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