package com.bimabagaskhoro.taskappphincon.utils

import android.content.Context
import android.widget.Toast
import java.io.IOException

class NoConnectivityException : IOException() {
    val messageNoConnectivityException : String
        get() = "No Internet Connection"
}