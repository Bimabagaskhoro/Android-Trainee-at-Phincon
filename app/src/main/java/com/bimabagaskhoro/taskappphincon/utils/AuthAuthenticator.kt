package com.bimabagaskhoro.taskappphincon.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiToken
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseRefreshToken
import com.bimabagaskhoro.taskappphincon.ui.activity.AuthActivity
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
                tokenManager.saveUserToken(newUserToken)
                tokenManager.saveRefreshToken(newRefreshToken)
                response.request.newBuilder()
                    .header("Authorization", it.success.access_token)
                    .build()
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