package com.bimabagaskhoro.phincon.core.data.source.repository.firebase.analytic


interface FirebaseAnalyticRepository {
    /**
     * splashscreen
     */
    fun onLoadSplashScreen(sc: String)

    /**
     * login
     */
    fun onClickButtonLogin(email: String)
    fun onClickButtonLoginToRegister()
    fun onLoadScreenLogin(sc: String)

    /**
     * register
     */
    fun onLoadScreenRegister(sc: String)
    fun onClickButtonRegisterToLogin()
    fun onClickCameraIcon()
    fun onChangeImage(image: String)
    fun onClickButtonRegister(
        image: String,
        email: String,
        name: String,
        phone: String,
        gender: String
    )

    /**
     * Home
     */
    fun onLoadScreenHome(sc: String)
    fun onPagingScrollHome(offset: String)
    fun onSearchHome(search: String)
    fun onClickProductHome(name: String, price: Double, rate: Int, id: Int)
    fun onClickNotificationToolbar()
    fun onClickTrolleyToolbar()

    /**
     * Favorite
     */
    fun onLoadScreenFavorite(sc: String)
    fun onSearchFavorite(search: String)
    fun onClickNotificationToolbarFavorite()
    fun onClickTrolleyToolbarFavorite()
    fun onClickSortByFavorite(param: String)
    fun onClickProductFavorite(name: String, price: Double, rate: Int, id: Int)

    /**
     * notification
     */
    fun onLoadScreenNotification(sc: String)
    fun onClickMultipleSelectIconNotification()
    fun onClickBackNotification()
    fun onClickItemNotification(tittle: String, message: String)
    fun onClickDeleteIconNotification(item: Int)
    fun onClickReadIconNotification(item: Int)
    fun onSelectCheckBoxNotification(tittle: String, message: String)

    /**
     * detail
     */
    fun onLoadScreenDetail(sc: String)
    fun onClickBtnTrolleyDetail()
    fun onClickBtnBuyDetail()
    fun onClickShareOnDetail(name: String, price: Double, id: Int)
    fun onClickLoveDetail(id: Int, name: String, status: String)
    fun onClickBackDetail()

    /**
     * bottom dialog detail
     */

    fun onShowPopupBottom(id: Int)
    fun onClickButtonQtyBottom(param: String, total: Int, id: Int, name: String)
    fun onClickButtonBuyBottom(param: String)
    fun onClickIconBankBottom(param: String)
    fun onClickButtonBuyWithBankBottom(
        param: String,
        id: Int,
        name: String,
        priceProd: Int,
        total: Int,
        totalPrice: Double,
        paymentMethode: String
    )

    /**
     * payment
     */

    fun onLoadScreenPayment(sc: String)
    fun onClickBackPayment()
    fun onClickBankPayment(paymentMethode: String, bank: String)

    /**
     * trolley
     */

    fun onLoadScreenTrolley(sc: String)
    fun onClickAddQtyTrolley(btnName: String, total: Int, id: Int, name: String)
    fun onClickDeleteTrolley(id: Int, name: String)
    fun onClickSelectTrolley(id: Int, name: String)
    fun onClickBuyOnTrolley()
    fun onClickBackTrolley()
    fun onClickIconBankTrolley(param: String)
    fun onClickBuyScsTrolley(price: Double, paymentMethode: String)

    /**
     * success page
     */

    fun onLoadScreenSuccessPage(sc: String)
    fun onClickBtnSuccessPage(rate: Int)

    /**
     * profile page
     */

    fun onLoadScreenProfile(sc: String)
    fun onChangeLanguageProfile(param: String)
    fun onClickChangePasswordProfile()
    fun onClickLogoutProfile()
    fun onChangeImageProfile(param: String)
    fun onClickCameraProfile()

    /**
     * change password
     */

    fun onLoadScreenPassword(sc: String)
    fun onClickSavePassword()
    fun onClickBackPassword()
}