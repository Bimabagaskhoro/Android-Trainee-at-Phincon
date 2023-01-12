package com.bimabagaskhoro.taskappphincon.data.source.network

import com.bimabagaskhoro.taskappphincon.data.source.response.*
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
}