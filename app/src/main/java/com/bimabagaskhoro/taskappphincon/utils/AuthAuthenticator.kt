package com.bimabagaskhoro.taskappphincon.utils

import android.content.Context
import android.content.Intent
import android.util.Log
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.data.source.network.ApiToken
import com.bimabagaskhoro.taskappphincon.data.source.response.auth.ResponseRefreshToken
import com.bimabagaskhoro.taskappphincon.ui.activity.AuthActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.*
import okhttp3.Authenticator
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: AuthPreferences,
    private val context: Context
): Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {

        return runBlocking {
            val accessToken = tokenManager.getAccessToken().first()
            val refreshToken = tokenManager.getRefreshToken().first()
            val userId = tokenManager.getUserId().first()

            val newToken = getNewToken(accessToken, refreshToken, userId)

            if (!newToken.isSuccessful || newToken.body() == null || newToken.code() == 401) {
                tokenManager.clear()
                //getNewToken(accessToken, refreshToken, userId)
                val intent = Intent(context, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }


            newToken.body()?.let {
                val newUserToken = it.success.access_token
                val newRefreshToken = it.success.refresh_token
                tokenManager.saveUserToken(newUserToken)
                tokenManager.saveRefreshToken(newRefreshToken)
                Log.d("newToken" , it.success.access_token)
                Log.d("newRefreshToken" , it.success.refresh_token)
                Log.d("resultAsddd" , tokenManager.saveRefreshToken(newRefreshToken).toString())
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
            .baseUrl("http://172.17.20.201/training_android/public/api/ecommerce/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
        val service = retrofit.create(ApiToken::class.java)
        return service.refreshToken(userId, accessToken, refreshToken )
    }
}