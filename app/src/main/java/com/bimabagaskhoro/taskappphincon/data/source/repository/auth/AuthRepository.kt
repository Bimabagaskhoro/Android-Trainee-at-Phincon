package com.bimabagaskhoro.taskappphincon.data.source.repository.auth

import com.bimabagaskhoro.taskappphincon.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestRating
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestStock
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangeImage
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangePassword
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseLogin
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.ResponseDetail
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseAddFavorite
import com.bimabagaskhoro.taskappphincon.utils.Resource
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import okhttp3.RequestBody

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

    fun changePassword(
        //token: String,
        id: Int,
        password: String,
        newPassword: String,
        confirmPassword: String
    ): Flow<Resource<ResponseChangePassword>>

    fun changeImage(
        //token: String,
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
}