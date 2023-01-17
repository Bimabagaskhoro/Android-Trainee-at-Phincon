package com.bimabagaskhoro.taskappphincon.utils

import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor @Inject constructor(
    private val tokenManager: AuthPreferences,
): Interceptor  {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.getAccessToken().first()
        }
        val request = chain.request().newBuilder()
        request.addHeader("Authorization", "$token")
        return chain.proceed(request.build())
    }
}