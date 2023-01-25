package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.*
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestRating
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangeImage
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.ResponseDetail
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseAddFavorite
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.repository.auth.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    fun login(
        email: String,
        password: String
    ) = authRepository.login(email, password).asLiveData()

    fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int,
    ): LiveData<Resource<ResponseRegister>> =
        authRepository.register(image, email, password, name, phone, gender).asLiveData()

    fun changePassword(
        id: Int,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) = authRepository.changePassword(id, oldPassword, newPassword, confirmPassword).asLiveData()

    fun changeImage(
        id: Int,
        image: MultipartBody.Part,
    ): LiveData<Resource<ResponseChangeImage>> =
        authRepository.changeImage(id, image).asLiveData()

    fun getDetail(
        idProduct: Int,
        idUser: Int
    ): LiveData<Resource<ResponseDetail>> =
        authRepository.getDetailProduct(idProduct, idUser).asLiveData()

    fun addFavorite(
        userId: Int,
        idProduct: Int
    ): LiveData<Resource<ResponseAddFavorite>> =
        authRepository.addFavorite(userId,idProduct).asLiveData()

    fun unFavorite(
        userId: Int,
        idProduct: Int
    ): LiveData<Resource<ResponseAddFavorite>> =
        authRepository.unFavorite(userId,idProduct).asLiveData()

//    fun updateStock(
//        idProduct: String,
//        stock: Int
//    ): LiveData<Resource<ResponseChangeImage>> =
//        authRepository.updateStock(RequestStock(idProduct, stock)).asLiveData()

    fun updateRating(
        userId: Int,
        rate: RequestRating
    ): LiveData<Resource<ResponseAddFavorite>> =
        authRepository.updateRate(userId,rate).asLiveData()


}