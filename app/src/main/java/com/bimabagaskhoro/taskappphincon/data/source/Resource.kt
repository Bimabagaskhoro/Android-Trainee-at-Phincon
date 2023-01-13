package com.bimabagaskhoro.taskappphincon.data.source

import okhttp3.ResponseBody

sealed class Resource <out T>(val data: T? = null, val message: String? = null) {
    class Success<out T>(data: T) : Resource<T>(data)
    //class Error<out T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Error(val isNetworkError: Boolean, val errorCode: Int?, val errorBody: ResponseBody?): Resource<Nothing>()
    class Loading<out T>(data: T? = null) : Resource<T>(data)
}
