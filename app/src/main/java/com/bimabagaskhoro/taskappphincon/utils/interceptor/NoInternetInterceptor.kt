package com.bimabagaskhoro.taskappphincon.utils.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import android.widget.Toast
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject


@Suppress("UNREACHABLE_CODE", "DEPRECATION")
class NoInternetInterceptor @Inject constructor(
    private val context: Context
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val noConnectivityException = NoConnectivityException()
        if (!isConnected) {
            throw noConnectivityException
            Toast.makeText(context, noConnectivityException.messageNoConnectivityException, Toast.LENGTH_SHORT)
                .show()
        }
        val builder: Request.Builder = chain.request().newBuilder()
        return chain.proceed(builder.build())
    }

    private val isConnected: Boolean
        get() {
            val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val netInfo = connectivityManager.activeNetworkInfo
            return netInfo != null && netInfo.isConnected

        }
}