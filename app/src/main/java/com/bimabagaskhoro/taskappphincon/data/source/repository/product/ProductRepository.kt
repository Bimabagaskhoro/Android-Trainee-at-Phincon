package com.bimabagaskhoro.taskappphincon.data.source.repository.product

import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseLogin
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.ResponseProduct
import com.bimabagaskhoro.taskappphincon.utils.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getDataProduct(
        search: String?= null
    ): Flow<Resource<ResponseProduct>>

    fun getDataFavProduct(
        userId: Int,
        search: String?= null
    ): Flow<Resource<ResponseFavorite>>
}