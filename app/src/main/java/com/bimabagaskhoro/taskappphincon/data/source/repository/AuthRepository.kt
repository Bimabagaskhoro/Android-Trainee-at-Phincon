package com.bimabagaskhoro.taskappphincon.data.source.repository

import androidx.paging.PagingData
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestRating
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestStock
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangeImage
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangePassword
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseLogin
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.ResponseDetail
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseAddFavorite
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.DataItemProduct
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.product.ResponseProduct
import com.bimabagaskhoro.taskappphincon.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface AuthRepository {
    fun getDataProduct(
        search: String?
    ): Flow<PagingData<DataItemProduct>>

    fun login(
        email: String,
        password: String,
        tokenFcm: String
    ): Flow<Resource<ResponseLogin>>

    fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int
    ): Flow<Resource<ResponseRegister>>

    fun changePassword(

        id: Int,
        password: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Resource<ResponseChangePassword>>

    fun changeImage(

        id: Int,
        image: MultipartBody.Part
    ): Flow<Resource<ResponseChangeImage>>

    fun addFavorite(
        userId: Int,
        idProduct: Int
    ): Flow<Resource<ResponseAddFavorite>>

    fun getDetailProduct(
        idProduct: Int,
        idUser: Int
    ): Flow<Resource<ResponseDetail>>

    fun updateStock(
        data: RequestStock
    ): Flow<Resource<ResponseAddFavorite>>

    fun unFavorite(
        userId: Int,
        idProduct: Int
    ): Flow<Resource<ResponseAddFavorite>>

    fun updateRate(
        userId: Int,
        rate: RequestRating
    ): Flow<Resource<ResponseAddFavorite>>

    fun getDataFavProduct(
        userId: Int,
        search: String? = null
    ): Flow<Resource<ResponseFavorite>>

    fun getOtherProduct(
        userId: Int
    ): Flow<Resource<ResponseProduct>>

    fun getHistoryProduct(
        userId: Int
    ): Flow<Resource<ResponseProduct>>
}