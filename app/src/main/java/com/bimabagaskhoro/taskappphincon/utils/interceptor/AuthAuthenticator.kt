package com.bimabagaskhoro.taskappphincon.utils.interceptor

import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiToken
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseRefreshToken
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.BASE_URL
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.Authenticator
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: AuthPreferences
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        return runBlocking {
            val accessToken = tokenManager.getAccessToken().first()
            val refreshToken = tokenManager.getRefreshToken().first()
            val userId = tokenManager.getUserId().first()

            val newToken = getNewToken(accessToken, refreshToken, userId)
            println("token $newToken")

            if (!newToken.isSuccessful || newToken.body() == null || newToken.code() == 401) {
                //response.close()
                tokenManager.clear()
            }

            newToken.body()?.let {
                val newUserToken = it.success.access_token
                val newRefreshToken = it.success.refresh_token
                newUserToken?.let { it1 -> tokenManager.saveUserToken(it1) }
                newRefreshToken?.let { it1 -> tokenManager.saveRefreshToken(it1) }
                it.success.access_token?.let { it1 ->
                    response.request.newBuilder()
                        .header("Authorization", it1)
                        .build()
                }
            }
        }
    }

    private suspend fun getNewToken(
        accessToken: String?,
        refreshToken: String?,
        userId: Int?
    ): retrofit2.Response<ResponseRefreshToken> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(ApiToken::class.java)
        return service.refreshToken(userId, accessToken, refreshToken)
    }
}