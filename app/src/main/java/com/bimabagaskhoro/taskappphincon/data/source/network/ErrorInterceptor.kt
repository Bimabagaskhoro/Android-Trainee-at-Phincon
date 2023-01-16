package com.bimabagaskhoro.taskappphincon.data.source.network

import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class ErrorInterceptor @Inject constructor() :  Interceptor {

    //    @Inject
//    lateinit var token : AuthPreferences
//
//    override fun intercept(chain: Interceptor.Chain): Response {
//        val request = chain.request().newBuilder()
//        val token = token.saveToken
//        if (token != null) {
//            request.header("Authorization", token)
//        } else {
//            //req new token here
//        }
//
//        return chain.proceed(request.build())
//    }
    override fun intercept(chain: Interceptor.Chain): Response {
        TODO("Not yet implemented")
    }
}