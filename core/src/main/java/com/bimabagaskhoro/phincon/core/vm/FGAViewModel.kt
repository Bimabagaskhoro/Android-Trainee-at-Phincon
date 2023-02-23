package com.bimabagaskhoro.phincon.core.vm

import androidx.lifecycle.ViewModel
import com.bimabagaskhoro.phincon.core.data.source.repository.firebase.analytic.FirebaseAnalyticRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FGAViewModel @Inject constructor(
    private val repository: FirebaseAnalyticRepository
) : ViewModel() {
    /**
     * splashscreen
     */

    fun onLoadSplashScreen(sc: String) {
        repository.onLoadSplashScreen(sc)
    }

    /**
     * login
     */

    fun onClickButtonLogin(email: String) {
        repository.onClickButtonLogin(email)
    }

    fun onClickButtonLoginToRegister() {
        repository.onClickButtonLoginToRegister()
    }

    fun onLoadScreenLogin(sc: String) {
        repository.onLoadScreenLogin(sc)
    }


    /**
     * register
     */
    fun onLoadScreenRegister(sc: String) {
        repository.onLoadScreenRegister(sc)
    }

    fun onClickButtonRegisterToLogin() {
        repository.onClickButtonRegisterToLogin()
    }

    fun onClickCameraIcon() {
        repository.onClickCameraIcon()
    }

    fun onChangeImage(image: String) {
        repository.onChangeImage(image)
    }

    fun onClickButtonRegister(
        image: String,
        email: String,
        name: String,
        phone: String,
        gender: String
    ) {
        repository.onClickButtonRegister(image, email, name, phone, gender)
    }

    /**
     * Home
     */
    fun onLoadScreenHome(sc: String) {
        repository.onLoadScreenHome(sc)
    }

    fun onPagingScrollHome(offset: String) {
        repository.onPagingScrollHome(offset)
    }

    fun onSearchHome(search: String) {
        repository.onSearchHome(search)
    }

    fun onClickProductHome(name: String, price: Double, rate: Int, id: Int) {
        repository.onClickProductHome(name, price, rate, id)
    }

    fun onClickNotificationToolbar() {
        repository.onClickNotificationToolbar()
    }

    fun onClickTrolleyToolbar() {
        repository.onClickTrolleyToolbar()
    }

    /**
     * Favorite
     */
    fun onLoadScreenFavorite(sc: String) {
        repository.onLoadScreenFavorite(sc)
    }

    fun onSearchFavorite(search: String) {
        repository.onSearchFavorite(search)
    }

    fun onClickNotificationToolbarFavorite() {
        repository.onClickNotificationToolbarFavorite()
    }

    fun onClickTrolleyToolbarFavorite() {
        repository.onClickTrolleyToolbarFavorite()
    }

    fun onClickSortByFavorite(param: String) {
        repository.onClickSortByFavorite(param)
    }

    fun onClickProductFavorite(name: String, price: Double, rate: Int, id: Int) {
        repository.onClickProductFavorite(name, price, rate, id)
    }

    /**
     * notification
     */
    fun onLoadScreenNotification(sc: String) {
        repository.onLoadScreenNotification(sc)
    }

    fun onClickMultipleSelectIconNotification() {
        repository.onClickMultipleSelectIconNotification()
    }

    fun onClickBackNotification() {
        repository.onClickBackNotification()
    }

    fun onClickItemNotification(tittle: String, message: String) {
        repository.onClickItemNotification(tittle, message)
    }

    fun onClickDeleteIconNotification(item: Int) {
        repository.onClickDeleteIconNotification(item)
    }

    fun onClickReadIconNotification(item: Int) {
        repository.onClickReadIconNotification(item)
    }

    fun onSelectCheckBoxNotification(tittle: String, message: String) {
        repository.onSelectCheckBoxNotification(tittle, message)
    }

    /**
     * detail
     */

    fun onLoadScreenDetail(sc: String) {
        repository.onLoadScreenDetail(sc)
    }

    fun onClickBtnTrolleyDetail() {
        repository.onClickBtnTrolleyDetail()
    }

    fun onClickBtnBuyDetail() {
        repository.onClickBtnBuyDetail()
    }

    fun onClickShareOnDetail(name: String, price: Double, id: Int) {
        repository.onClickShareOnDetail(name, price, id)
    }

    fun onClickLoveDetail(id: Int, name: String, status: String) {
        repository.onClickLoveDetail(id, name, status)
    }

    fun onClickBackDetail() {
        repository.onClickBackDetail()
    }

    /**
     * bottom dialog detail
     */

    fun onShowPopupBottom(id: Int) {
        repository.onShowPopupBottom(id)
    }

    fun onClickButtonQtyBottom(param: String, total: Int, id: Int, name: String) {
        repository.onClickButtonQtyBottom(param, total, id, name)
    }

    fun onClickButtonBuyBottom(param: String) {
        repository.onClickButtonBuyBottom(param)
    }

    fun onClickIconBankBottom(param: String) {
        repository.onClickIconBankBottom(param)
    }

    fun onClickButtonBuyWithBankBottom(
        param: String, id: Int, name: String,
        priceProd: Int, total: Int, totalPrice: Double,
        paymentMethode: String) { repository.onClickButtonBuyWithBankBottom(
            param, id, name, priceProd, total, totalPrice, paymentMethode
        )
    }

    /**
     * payment
     */
    fun onLoadScreenPayment(sc: String) {
        repository.onLoadScreenPayment(sc)
    }

    fun onClickBackPayment() {
        repository.onClickBackPayment()
    }

    fun onClickBankPayment(paymentMethode: String, bank: String) {
        repository.onClickBankPayment(paymentMethode, bank)
    }

    /**
     * trolley
     */

    fun onLoadScreenTrolley(sc: String) {
        repository.onLoadScreenTrolley(sc)
    }

    fun onClickAddQtyTrolley(btnName: String, total: Int, id: Int, name: String) {
        repository.onClickAddQtyTrolley(btnName, total, id, name)
    }

    fun onClickDeleteTrolley(id: Int, name: String) {
        repository.onClickDeleteTrolley(id, name)
    }

    fun onClickSelectTrolley(id: Int, name: String) {
        repository.onClickSelectTrolley(id, name)
    }

    fun onClickBuyOnTrolley() {
        repository.onClickBuyOnTrolley()
    }

    fun onClickBackTrolley() {
        repository.onClickBackTrolley()
    }

    fun onClickIconBankTrolley(param: String) {
        repository.onClickIconBankTrolley(param)
    }

    fun onClickBuyScsTrolley(price: Double, paymentMethode: String) {
        repository.onClickBuyScsTrolley(price, paymentMethode)
    }

    /**
     * success page
     */

    fun onLoadScreenSuccessPage(sc: String) {
        repository.onLoadScreenSuccessPage(sc)
    }

    fun onClickBtnSuccessPage(rate: Int) {
        repository.onClickBtnSuccessPage(rate)
    }

    /**
     * profile page
     */

    fun onLoadScreenProfile(sc: String) {
        repository.onLoadScreenProfile(sc)
    }

    fun onChangeLanguageProfile(param: String) {
        repository.onChangeLanguageProfile(param)
    }

    fun onClickChangePasswordProfile() {
        repository.onClickChangePasswordProfile()
    }

    fun onClickLogoutProfile() {
        repository.onClickLogoutProfile()
    }

    fun onChangeImageProfile(param: String) {
        repository.onChangeImageProfile(param)
    }

    fun onClickCameraProfile() {
        repository.onClickCameraProfile()
    }

    /**
     * change password
     */

    fun onLoadScreenPassword(sc: String) {
        repository.onLoadScreenPassword(sc)
    }

    fun onClickSavePassword() {
        repository.onClickSavePassword()
    }

    fun onClickBackPassword() {
        repository.onClickBackPassword()
    }
}