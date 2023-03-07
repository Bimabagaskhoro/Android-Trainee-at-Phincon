package com.bimabagaskhoro.phincon.router

import android.content.Context
import android.content.Intent

interface ActivityRouter {
    fun toAuthActivity(context: Context): Intent
    fun toHomeActivity(context: Context): Intent
    fun toDetailActivity(context: Context, id: Int): Intent
    fun toNotificationActivity(context: Context): Intent
    fun toPasswordActivity(context: Context): Intent
    fun toTrolleyActivity(context: Context): Intent
    fun toPaymentFromTrolleyActivity(context: Context): Intent
    fun toSuccessPageFromTrolleyActivity(
        context: Context,
        listOfProductId: ArrayList<String>,
        dataName: String,
        dataPayment: String,
        resultPrice: String
    ): Intent

    fun toSuccessPageFromDetailActivity(
        context: Context,
        idProduct: Int,
        dataName: String,
        dataPayment: String,
        resultPrice: String
    ): Intent

    fun toTrolleyFromPaymentActivity(
        context: Context,
        dataId: String,
        dataName: String
    ): Intent

    fun toPaymentFromDetailActivity(context: Context, id: Int): Intent
    fun toDetailFromPaymentActivity(
        context: Context,
        id: Int,
        idPayment: String,
        namePayment: String
    ): Intent
}