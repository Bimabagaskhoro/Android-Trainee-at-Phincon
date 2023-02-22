package com.bimabagaskhoro.taskappphincon.utils.interceptor

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import androidx.annotation.RequiresApi
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ConnectionManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : ConnectionManager {
    @RequiresApi(Build.VERSION_CODES.M)
    private val connectivityManager =
        context.getSystemService(ConnectivityManager::class.java) as ConnectivityManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun isConnected(): Boolean {
        return connectivityManager.activeNetwork != null
    }
}