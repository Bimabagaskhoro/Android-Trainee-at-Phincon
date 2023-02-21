package com.bimabagaskhoro.taskappphincon.utils

import okhttp3.ResponseBody

sealed class Resource<out T>(val data: T? = null, val message: String? = null) {
    class Success<out T>(data: T) : Resource<T>(data)
    class Error(
        val errorMessage: String?,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ) : Resource<Nothing>()

    class Loading<out T>(data: T? = null) : Resource<T>(data)
    class Empty : Resource<Nothing>()
}
