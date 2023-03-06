package com.bimabagaskhoro.taskappphincon.router

import android.content.Context
import android.content.Intent
import com.bimabagaskhoro.phincon.router.ActivityRouter
import com.bimabagaskhoro.phincon.features.auth.AuthActivity
import com.bimabagaskhoro.phincon.features.changepassword.PasswordActivity
import com.bimabagaskhoro.phincon.features.main.MainActivity
import com.bimabagaskhoro.phincon.features.trolley.CartActivity
import com.bimabagaskhoro.phincon.features.detail.DetailActivity
import com.bimabagaskhoro.phincon.features.notification.NotificationActivity
import com.bimabagaskhoro.phincon.features.succespage.OnSuccessActivity
import com.bimabagaskhoro.phincon.features.payment.PaymentActivity

class ActivityRouterImpl : ActivityRouter {
    override fun toAuthActivity(context: Context): Intent {
        return Intent(context, AuthActivity::class.java)
    }

    override fun toHomeActivity(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    override fun toDetailActivity(context: Context, id: Int): Intent {
        return DetailActivity.createIntentById(context, id)
    }

    override fun toNotificationActivity(context: Context): Intent {
        return Intent(context, NotificationActivity::class.java)
    }

    override fun toPasswordActivity(context: Context): Intent {
        return Intent(context, PasswordActivity::class.java)
    }

    override fun toTrolleyActivity(context: Context): Intent {
        return Intent(context, CartActivity::class.java)
    }

    override fun toPaymentFromTrolleyActivity(context: Context): Intent {
        return Intent(context, PaymentActivity::class.java)
    }

    override fun toSuccessPageFromTrolleyActivity(
        context: Context,
        listOfProductId: ArrayList<String>,
        dataName: String,
        dataPayment: String,
        resultPrice: String
    ): Intent {
        return OnSuccessActivity.createIntentFromTrolley(
            context,
            listOfProductId,
            dataName,
            dataPayment,
            resultPrice
        )
    }

    override fun toSuccessPageFromDetailActivity(
        context: Context,
        idProduct: Int,
        dataName: String,
        dataPayment: String,
        resultPrice: Int
    ): Intent {
        return OnSuccessActivity.createIntentFromDetail(
            context, idProduct, dataName, dataPayment, resultPrice
        )
    }

    override fun toTrolleyFromPaymentActivity(
        context: Context,
        dataId: String,
        dataName: String
    ): Intent {
        return CartActivity.createIntentFromPayment(context, dataId, dataName)
    }

    override fun toPaymentFromDetailActivity(context: Context, id: Int): Intent {
        return PaymentActivity.createIntentFromDetail(context, id)
    }

    override fun toDetailFromPaymentActivity(
        context: Context,
        id: Int,
        idPayment: String,
        namePayment: String
    ): Intent {
        return DetailActivity.createIntentFromPayment(context, id, idPayment, namePayment)
    }

}