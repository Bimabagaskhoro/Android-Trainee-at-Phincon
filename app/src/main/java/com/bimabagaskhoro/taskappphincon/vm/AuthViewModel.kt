package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.*
import androidx.paging.cachedIn
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.DataStockItem
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestRating
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.RequestStock
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseChangeImage
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.auth.ResponseRegister
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.detail.ResponseDetail
import com.bimabagaskhoro.taskappphincon.data.source.remote.response.favorite.ResponseAddFavorite
import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.bimabagaskhoro.taskappphincon.data.source.repository.remote.RemoteRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository,
    private val dataPreference: AuthPreferences
) : ViewModel() {
    fun login(
        email: String,
        password: String,
        tokenFcm: String
    ) = remoteRepository.login(email, password, tokenFcm).asLiveData()

    fun register(
        image: MultipartBody.Part,
        email: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        gender: Int,
    ): LiveData<Resource<ResponseRegister>> =
        remoteRepository.register(image, email, password, name, phone, gender).asLiveData()

    fun changePassword(
        id: Int,
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ) = remoteRepository.changePassword(id, oldPassword, newPassword, confirmPassword).asLiveData()

    fun changeImage(
        id: Int,
        image: MultipartBody.Part,
    ): LiveData<Resource<ResponseChangeImage>> =
        remoteRepository.changeImage(id, image).asLiveData()

    fun getDetail(
        idProduct: Int,
        idUser: Int
    ): LiveData<Resource<ResponseDetail>> =
        remoteRepository.getDetailProduct(idProduct, idUser).asLiveData()

    fun addFavorite(
        userId: Int,
        idProduct: Int
    ): LiveData<Resource<ResponseAddFavorite>> =
        remoteRepository.addFavorite(userId, idProduct).asLiveData()

    fun unFavorite(
        userId: Int,
        idProduct: Int
    ): LiveData<Resource<ResponseAddFavorite>> =
        remoteRepository.unFavorite(userId, idProduct).asLiveData()

    fun updateStock(
        data: List<DataStockItem>,
        idUser: String
    ): LiveData<Resource<ResponseAddFavorite>> =
        remoteRepository.updateStock(
            RequestStock(idUser, data)
        ).asLiveData()

    fun updateRating(
        userId: Int,
        rate: RequestRating
    ): LiveData<Resource<ResponseAddFavorite>> =
        remoteRepository.updateRate(userId, rate).asLiveData()

    fun getOtherProduct(
        userId: Int
    ) = remoteRepository.getOtherProduct(userId).asLiveData()

    fun getHistoryProduct(
        userId: Int
    ) = remoteRepository.getHistoryProduct(userId).asLiveData()

    /**
     * for home fragment
     */
    private var searchJob: Job? = null
    private val currentQuery = MutableStateFlow("")

    fun onSearch(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            if (query.isEmpty()) {
                currentQuery.value = query
            } else {
                delay(2000)
                currentQuery.value = query
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val productList = currentQuery.flatMapLatest {
        remoteRepository.getDataProduct(it).cachedIn(viewModelScope)
    }

    /**
     * until here home fragment
     */
    fun getFavProduct(
        userId: Int,
        search: String? = null
    ) = remoteRepository.getDataFavProduct(userId, search).asLiveData()

    fun onRefresh() {
        viewModelScope.launch {
            dataPreference.getUserId().first()?.let { getFavProduct(it,"") }
        }
    }

    /**
     * for favorite fragment
     */

}