package com.bimabagaskhoro.taskappphincon.utils

import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class HeaderInterceptor @Inject constructor(): Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", "TuIBt77u7tZHi8n7WqUC")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}