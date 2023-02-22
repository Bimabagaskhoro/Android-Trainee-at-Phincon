package com.bimabagaskhoro.taskappphincon.utils.interceptor

import android.content.Context
import android.widget.Toast
import okhttp3.Interceptor
import okhttp3.Response
import okio.IOException
import javax.inject.Inject

class NoInternetInterceptor @Inject constructor(
    private val connectionManager: ConnectionManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (connectionManager.isConnected()) {
            chain.proceed(chain.request())
        } else {
            throw IOException("No Internet Available")
        }
    }
}