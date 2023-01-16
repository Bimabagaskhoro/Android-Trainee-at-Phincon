package com.bimabagaskhoro.taskappphincon.utils

import okhttp3.Interceptor
import okhttp3.Response

class HeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()
            .header("apikey", "TuIBt77u7tZHi8n7WqUC")
        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}