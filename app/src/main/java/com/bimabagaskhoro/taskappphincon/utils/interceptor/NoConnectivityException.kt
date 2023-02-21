package com.bimabagaskhoro.taskappphincon.utils.interceptor

import java.io.IOException

class NoConnectivityException : IOException() {
    val messageNoConnectivityException : String
        get() = "No Internet Connection"
}