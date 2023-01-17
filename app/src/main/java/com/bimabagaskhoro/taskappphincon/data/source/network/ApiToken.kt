package com.bimabagaskhoro.taskappphincon.data.source.network

import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseRefreshToken
import retrofit2.Response
import retrofit2.http.*

interface ApiToken {
    @FormUrlEncoded
    @Headers(*["apikey: TuIBt77u7tZHi8n7WqUC"])
    @POST("refresh-token")
    suspend fun refreshToken(
        @Field("id_user") id: Int?,
        @Field("access_token") accessToken: String?,
        @Field("refresh_token") refreshToken: String?
    ): Response<ResponseRefreshToken>
}