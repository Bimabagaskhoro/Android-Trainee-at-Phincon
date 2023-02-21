package com.bimabagaskhoro.taskappphincon.utils.interceptor

import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(
    private val tokenManager: AuthPreferences
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking {
            tokenManager.getAccessToken().first()
        }
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", "TuIBt77u7tZHi8n7WqUC")
            .header("Authorization", "$token")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}