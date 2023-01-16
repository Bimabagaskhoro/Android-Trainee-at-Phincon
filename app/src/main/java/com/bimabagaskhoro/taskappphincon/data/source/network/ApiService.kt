package com.bimabagaskhoro.taskappphincon.data.source.network

import com.bimabagaskhoro.taskappphincon.data.source.response.*
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @Headers(*["apikey: TuIBt77u7tZHi8n7WqUC"])
    @POST("authentication")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): ResponseLogin

    @Multipart
    @Headers(*["apikey: TuIBt77u7tZHi8n7WqUC"])
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
    @Headers(*["apikey: TuIBt77u7tZHi8n7WqUC"])
    @POST("change-password/{id}")
    suspend fun changePassword(
        @Header("Authorization") token : String,
        @Path("id") id: Int,
        @Field("password") password: String,
        @Field("new_password") newPassword: String,
        @Field("confirm_password") confirmPassword: String
    ): ResponseChangePassword

    @Multipart
    @Headers(*["apikey: TuIBt77u7tZHi8n7WqUC"])
    @POST("change-image")
    suspend fun changeImage(
        @Header("Authorization") token : String,
        @Part("id") id: Int,
        @Part image: MultipartBody.Part,
    ): ResponseChangeImage

    @FormUrlEncoded
    @Headers(*["apikey: TuIBt77u7tZHi8n7WqUC"])
    @POST("refresh-token")
    suspend fun refreshToken(
        @Path("id") idUser: Int,
        @Field("access_token") accessToken: String,
        @Field("refresh_token") refreshToken: String
    ): ResponseRefreshToken
}