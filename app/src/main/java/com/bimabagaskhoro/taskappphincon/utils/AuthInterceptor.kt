package com.bimabagaskhoro.taskappphincon.utils

import android.content.Context
import android.content.Intent
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.ui.activity.AuthActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: AuthPreferences,
    private val context: Context
): Interceptor  {
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val token = runBlocking {
            tokenManager.getAccessToken().first()
        }
        if (token != null) {
            request = request.newBuilder()
                .addHeader("Authorization", "$token")
                .build()
        }

        /**
         * change access token to refresh token
         */
        val response = chain.proceed(request)
        if (response.code == 401) {
            val refreshToken = runBlocking {
                tokenManager.getRefreshToken().first()
            }
            if (refreshToken != null) {
                request = request.newBuilder()
                    .addHeader("Authorization", "$refreshToken")
                    .build()
                return chain.proceed(request)
            }
            else {
                runBlocking { tokenManager.clear() }
                val intent = Intent(context, AuthActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
            response.close()
        }
        return response

//        val token = runBlocking {
//            tokenManager.getAccessToken().first()
//        }
//        val request = chain.request().newBuilder()
//        request.addHeader("Authorization", "$token")
//        return chain.proceed(request.build())
    }
}