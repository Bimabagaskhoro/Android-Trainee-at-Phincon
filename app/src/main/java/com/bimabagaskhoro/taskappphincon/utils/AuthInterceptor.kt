package com.bimabagaskhoro.taskappphincon.utils

import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor : Interceptor {
    lateinit var authPreferences: AuthPreferences

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            authPreferences.getAccessToken().first()
        }
        val request = chain.request().newBuilder()
        request.addHeader("Authorization", "$token")
        return chain.proceed(request.build())
    }
}