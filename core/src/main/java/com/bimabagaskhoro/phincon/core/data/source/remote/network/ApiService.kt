package com.bimabagaskhoro.phincon.core.data.source.remote.network

import com.bimabagaskhoro.phincon.core.data.source.remote.response.RequestRating
import com.bimabagaskhoro.phincon.core.data.source.remote.response.RequestStock
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.ResponseChangeImage
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.ResponseChangePassword
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.ResponseLogin
import com.bimabagaskhoro.phincon.core.data.source.remote.response.auth.ResponseRegister
import com.bimabagaskhoro.phincon.core.data.source.remote.response.detail.ResponseDetail
import com.bimabagaskhoro.phincon.core.data.source.remote.response.favorite.ResponseAddFavorite
import com.bimabagaskhoro.phincon.core.data.source.remote.response.favorite.ResponseFavorite
import com.bimabagaskhoro.phincon.core.data.source.remote.response.product.ResponseProduct
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("authentication")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String,
        @Field("token_fcm") tokenFcm: String
    ): ResponseLogin

    @Multipart
    @POST("registration")
    suspend fun register(
        @Part image: MultipartBody.Part,
        @Part("email") email: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part("gender") gender: Int
    ): ResponseRegister

    @FormUrlEncoded
    @PUT("change-password/{id}")
    suspend fun changePassword(
        @Path("id") id: Int,
        @Field("password") password: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_password") confirmPassword: String
    ): ResponseChangePassword

    @Multipart
    @POST("change-image")
    suspend fun changeImage(
        @Part("id") id: Int,
        @Part image: MultipartBody.Part
    ): ResponseChangeImage

    @FormUrlEncoded
    @POST("add_favorite")
    suspend fun addFavorite(
        @Field("id_product") id_product: Int,
        @Field("id_user") id_user: Int
    ): ResponseAddFavorite

    @FormUrlEncoded
    @POST("remove_favorite")
    suspend fun unFavorite(
        @Field("id_product") id_product: Int,
        @Field("id_user") id_user: Int
    ): ResponseAddFavorite

    @GET("get_detail_product")
    suspend fun getDetail(
        @Query("id_product") id_product: Int,
        @Query("id_user") id_user: Int,
    ): ResponseDetail

//    @Multipart
    @POST("update-stock")
    suspend fun updateStock(
        @Body data: RequestStock
    ): ResponseAddFavorite

    @PUT("update_rate/{id}")
    suspend fun updateRating(
        @Path("id") id: Int,
        @Body requestRating: RequestRating
    ): ResponseAddFavorite

    @GET("get_list_product_favorite")
    suspend fun getListFav(
        @Query("id_user") id_user: Int,
        @Query("search") search: String? = null
    ): ResponseFavorite

    @GET("get_list_product_paging")
    suspend fun getListProduct(
        @Query("search") search: String? = null,
        @Query("offset") offset: Int
    ): ResponseProduct

    @GET("get_list_product_other")
    suspend fun getOtherProduct(
        @Query("id_user") idUser: Int
    ): ResponseProduct

    @GET("get_list_product_riwayat")
    suspend fun getHistoryProduct(
        @Query("id_user") idUser: Int
    ): ResponseProduct
}