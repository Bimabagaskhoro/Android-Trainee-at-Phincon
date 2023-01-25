package com.bimabagaskhoro.taskappphincon.data.source.repository.product

import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiPaging
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.ResponseProduct
import com.bimabagaskhoro.taskappphincon.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiPaging: ApiPaging
) : ProductRepository {
    override fun getDataProduct(search: String?): Flow<Resource<ResponseProduct>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiPaging.getListProduct(search)
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

    override fun getDataFavProduct(
        userId: Int,
        search: String?
    ): Flow<Resource<ResponseFavorite>> {
        return flow {
            emit(Resource.Loading())
            try {
                val response = apiPaging.getListFav(userId, search)
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