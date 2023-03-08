package com.bimabagaskhoro.phincon.core.utils.interceptor

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import com.bimabagaskhoro.phincon.core.data.pref.AuthPreferences
import com.bimabagaskhoro.phincon.router.ActivityRouter
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthBadResponse @Inject constructor(
    private val tokenManager: AuthPreferences,
    private val context: Context
) : Interceptor {
    @Inject
    lateinit var router: ActivityRouter
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        try {
            if (response.code == 401) {
                runBlocking {
                    tokenManager.clear()
                }
                val intent = Intent(context, Class.forName("com.bimabagaskhoro.phincon.features.auth.AuthActivity"))
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                return response
            }
        } catch (e: Exception) {
            Toast.makeText(context, "No Internet Detect", Toast.LENGTH_SHORT).show()
        }
        return response
    }
}