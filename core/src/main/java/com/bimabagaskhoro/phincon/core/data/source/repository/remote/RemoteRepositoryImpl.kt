package com.bimabagaskhoro.phincon.core.data.source.repository.remote

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.bimabagaskhoro.phincon.core.data.source.remote.ProductPagingSource
import com.bimabagaskhoro.phincon.core.data.source.remote.network.ApiService
import com.bimabagaskhoro.phincon.core.data.source.remote.response.RequestRating
import com.bimabagaskhoro.phincon.core.data.source.remote.response.RequestStock
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.ResponseChangeImage
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.ResponseChangePassword
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.ResponseLogin
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.ResponseRegister
import com.bimabagaskhoro.phincon.core.data.source.remote.response.detail.ResponseDetail
import com.bimabagaskhoro.phincon.core.data.source.remote.response.favorite.ResponseAddFavorite
import com.bimabagaskhoro.phincon.core.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.ResponseProduct
import com.bimabagaskhoro.phincon.core.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class RemoteRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : RemoteRepository {
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
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
                val response =
                    apiService.changePassword(id, password, newPassword, confirmPassword)
                emit(Resource.Success(response))
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
                val response = apiService.updateStock(data)
                emit(Resource.Success(response))
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
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
                emit(Resource.Success(response))
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
                }
            }
        }
    }

    override fun getOtherProduct(userId: Int): Flow<Resource<ResponseProduct>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.getOtherProduct(userId)
                emit(Resource.Success(response))
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
                }
            }
        }
    }

    override fun getHistoryProduct(userId: Int): Flow<Resource<ResponseProduct>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiService.getHistoryProduct(userId)
                emit(Resource.Success(response))
            } catch (t: Throwable) {
                when (t) {
                    is HttpException -> {
                        when (t.code()) {
                            400 -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                            else -> emit(Resource.Error(t.message, t.code(), t.response()?.errorBody()))
                        }
                    }
                    is Exception -> {
                        emit(Resource.Error(t.message, null, null))
                    }
                }
            }
        }
    }
}