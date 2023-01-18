package com.bimabagaskhoro.taskappphincon.data.source.remote.network

import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.ResponseProduct
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiPaging {
    @GET("get_list_product")
    suspend fun getListProduct(): ResponseProduct

    @GET("get_list_product_favorite")
    suspend fun getListFav(
        @Query("search") search: String,
        @Query("id_user") id_user: Int
    ): ResponseFavorite
}