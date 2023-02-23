package com.bimabagaskhoro.phincon.core.data.source.repository.firebase.analytic

import android.os.Bundle
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.BUTTON_CLICK
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.BUTTON_NAME
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.IMAGE_HLP
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.ON_SCROLL
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.ON_SEARCH
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.POPUP_DETAIL
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.POPUP_SORT
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.SCREEN_CLASS
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.SCREEN_NAME
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.SCREEN_VIEW
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.SELECT_ITEM
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

class FirebaseAnalyticRepositoryImpl @Inject constructor(
    private val analytics: FirebaseAnalytics
) : FirebaseAnalyticRepository {
    /**
     * Splash Screen
     */
    override fun onLoadSplashScreen(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Splash Screen")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    /**
     * Login Fragment
     */
    override fun onClickButtonLogin(email: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Login")
            putString("email", email)
            putString(BUTTON_NAME, "Login")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickButtonLoginToRegister() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Login")
            putString(BUTTON_NAME, "Sign Up")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onLoadScreenLogin(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Login")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    /**
     * Register Fragment
     */
    override fun onLoadScreenRegister(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Sign Up")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onClickButtonRegisterToLogin() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Sign Up")
            putString(BUTTON_NAME, "Login")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickCameraIcon() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Sign Up")
            putString(BUTTON_NAME, "Icon Photo")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onChangeImage(image: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Profile")
            putString(IMAGE_HLP, image)
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    override fun onClickButtonRegister(
        image: String,
        email: String,
        name: String,
        phone: String,
        gender: String
    ) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Sign Up")
            putString(IMAGE_HLP, image)
            putString("email", email)
            putString("name", name)
            putString("phone", phone)
            putString("gender", gender)
            putString(BUTTON_NAME, "Sign Up")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    /**
     * Home Fragment
     */

    override fun onLoadScreenHome(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Home")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onPagingScrollHome(offset: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Home")
            putString("page", offset)
        }
        analytics.logEvent(ON_SCROLL, bundle)
    }

    override fun onSearchHome(search: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Home")
            putString("search", search)
        }
        analytics.logEvent(ON_SEARCH, bundle)
    }

    override fun onClickProductHome(name: String, price: Double, rate: Int, id: Int) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Home")
            putString("product_name", name)
            putDouble("product_price", price)
            putInt("product_rate", rate)
            putInt("product_id", id)
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    override fun onClickNotificationToolbar() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Home")
            putString(BUTTON_NAME, "Notif Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickTrolleyToolbar() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Home")
            putString(BUTTON_NAME, "Trolley Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    /**
     * Favorite Fragment
     */

    override fun onLoadScreenFavorite(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Favorite")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onSearchFavorite(search: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Favorite")
            putString("search", search)
        }
        analytics.logEvent(ON_SEARCH, bundle)
    }

    override fun onClickNotificationToolbarFavorite() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Favorite")
            putString(BUTTON_NAME, "Notif Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickTrolleyToolbarFavorite() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Favorite")
            putString(BUTTON_NAME, "Trolley Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickSortByFavorite(param: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Favorite")
            putString("sort_by", param)
        }
        analytics.logEvent(POPUP_SORT, bundle)
    }

    override fun onClickProductFavorite(name: String, price: Double, rate: Int, id: Int) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Favorite")
            putString("product_name", name)
            putDouble("product_price", price)
            putInt("product_rate", rate)
            putInt("product_id", id)
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    /**
     * Notification Activity
     */

    override fun onLoadScreenNotification(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Notification")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onClickMultipleSelectIconNotification() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Notification")
            putString(BUTTON_NAME, "Multiple Select Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickBackNotification() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Notification")
            putString(BUTTON_NAME, "Back Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickItemNotification(tittle: String, message: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Notification")
            putString("title", tittle)
            putString("message", message)
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    override fun onClickDeleteIconNotification(item: Int) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Multiple Select")
            putString(BUTTON_NAME, "delete Icon")
            putInt("total_select_item", item)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickReadIconNotification(item: Int) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Multiple Select")
            putString(BUTTON_NAME, "read Icon")
            putInt("total_select_item", item)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onSelectCheckBoxNotification(tittle: String, message: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Multiple Select")
            putString("title", tittle)
            putString("message", message)
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    /**
     * Detail Activity
     */
    override fun onLoadScreenDetail(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onClickBtnTrolleyDetail() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString(BUTTON_NAME, "+ Trolley")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickBtnBuyDetail() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString(BUTTON_NAME, "Buy")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickShareOnDetail(name: String, price: Double, id: Int) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString("product_name", name)
            putDouble("product_price", price)
            putInt("product_id", id)
            putString(BUTTON_NAME, "Share Product")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickLoveDetail(id: Int, name: String, status: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putInt("product_id", id)
            putString("product_name", name)
            putString("status", status)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickBackDetail() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString(BUTTON_NAME, "Back Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    /**
     * bottom dialog detail
     */

    override fun onShowPopupBottom(id: Int) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString("popup", "show")
            putInt("product_id", id)
        }
        analytics.logEvent(POPUP_DETAIL, bundle)
    }

    override fun onClickButtonQtyBottom(param: String, total: Int, id: Int, name: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString(BUTTON_NAME, param)
            putInt("total", total)
            putInt("product_id", id)
            putString("product_name", name)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickButtonBuyBottom(param: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString(BUTTON_NAME, param)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickIconBankBottom(param: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString(BUTTON_NAME, param)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickButtonBuyWithBankBottom(
        param: String,
        id: Int,
        name: String,
        priceProd: Int,
        total: Int,
        totalPrice: Double,
        paymentMethode: String
    ) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Detail Product")
            putString(BUTTON_NAME, param)
            putInt("product_id", id)
            putString("product_name", name)
            putInt("product_price", priceProd)
            putInt("product_total", total)
            putDouble("product_totalprice", totalPrice)
            putString("payment_method", paymentMethode)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    /**
     * payment
     */

    override fun onLoadScreenPayment(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Pilih Metode Pembayaran")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onClickBackPayment() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Pilih Metode Pembayaran")
            putString(BUTTON_NAME, "Back Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickBankPayment(paymentMethode: String, bank: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Pilih Metode Pembayaran")
            putString("jenis_pembayaran", paymentMethode)
            putString("bank", bank)
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    /**
     * trolley
     */

    override fun onLoadScreenTrolley(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Trolley")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onClickAddQtyTrolley(btnName: String, total: Int, id: Int, name: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Trolley")
            putString(BUTTON_NAME, btnName)
            putInt("total", total)
            putInt("product_id", id)
            putString("product_name", name)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickDeleteTrolley(id: Int, name: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Trolley")
            putString(BUTTON_NAME, "Delete Icon")
            putInt("product_id", id)
            putString("product_name", name)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickSelectTrolley(id: Int, name: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Trolley")
            putInt("product_id", id)
            putString("product_name", name)
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    override fun onClickBuyOnTrolley() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Trolley")
            putString(BUTTON_NAME, "Buy")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickBackTrolley() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Trolley")
            putString(BUTTON_NAME, "Back Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickIconBankTrolley(param: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Trolley")
            putString(BUTTON_NAME, param)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickBuyScsTrolley(price: Double, paymentMethode: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Trolley")
            putString(BUTTON_NAME, "Buy")
            putDouble("total_price", price)
            putString("payment_method", paymentMethode)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }


    /**
     * succes page
     */

    override fun onLoadScreenSuccessPage(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Success")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onClickBtnSuccessPage(rate: Int) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Success")
            putString(BUTTON_NAME, "Submit")
            putInt("rate", rate)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    /**
     * profile page
     */

    override fun onLoadScreenProfile(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Profile")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onChangeLanguageProfile(param: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Profile")
            putString("item_name", "Change Language")
            putString("language", param)
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    override fun onClickChangePasswordProfile() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Profile")
            putString("item_name", "Change Password")
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    override fun onClickLogoutProfile() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Profile")
            putString("item_name", "Logout")
        }
        analytics.logEvent(SELECT_ITEM, bundle)
    }

    override fun onChangeImageProfile(param: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Profile")
            putString("image", param)
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickCameraProfile() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Profile")
            putString(BUTTON_NAME, "Icon Photo")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    /**
     * change password
     */
    override fun onLoadScreenPassword(sc: String) {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Change Password")
            putString(SCREEN_CLASS, sc)
        }
        analytics.logEvent(SCREEN_VIEW, bundle)
    }

    override fun onClickSavePassword() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Change Password")
            putString(BUTTON_NAME, "Save")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

    override fun onClickBackPassword() {
        val bundle = Bundle().apply {
            putString(SCREEN_NAME, "Change Password")
            putString(BUTTON_NAME, "Back Icon")
        }
        analytics.logEvent(BUTTON_CLICK, bundle)
    }

}


