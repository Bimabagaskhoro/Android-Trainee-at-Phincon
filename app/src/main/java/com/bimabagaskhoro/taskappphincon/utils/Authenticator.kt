package com.bimabagaskhoro.taskappphincon.utils

import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.data.source.network.ApiAuth
import com.bimabagaskhoro.taskappphincon.data.source.network.ApiService
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseRefreshToken
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.Authenticator
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class Authenticator : Authenticator {
    lateinit var tokenManager: AuthPreferences
    override fun authenticate(route: Route?, response: Response): Request? {
        val accessToken = runBlocking {
            tokenManager.getAccessToken().first()
        }

        val refreshToken = runBlocking {
            tokenManager.getRefreshToken().first()
        }

        val userId = runBlocking {
            tokenManager.getUserId().first()
        }

        return runBlocking {
            val newToken = getNewToken(accessToken, refreshToken, userId)

            if (!newToken.isSuccessful || newToken.body() == null) { //Couldn't refresh the token, so restart the login process
                tokenManager.clear()
            }

            newToken.body()?.let {
                tokenManager.saveUserToken(it.success.access_token)
                response.request.newBuilder()
                    .header("Authorization", it.success.access_token)
                    .build()
            }
        }
    }

    private suspend fun getNewToken(accessToken: String?, refreshToken: String?, userId: Int?): retrofit2.Response<ResponseRefreshToken> {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        val okHttpClient = OkHttpClient.Builder().addInterceptor(loggingInterceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.17.20.201/training_android/public/api/ecommerce/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(ApiAuth::class.java)
        return service.refreshToken(userId, accessToken, refreshToken )
    }
}