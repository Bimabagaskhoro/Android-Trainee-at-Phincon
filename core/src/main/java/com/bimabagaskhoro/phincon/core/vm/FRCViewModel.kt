package com.bimabagaskhoro.phincon.core.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bimabagaskhoro.phincon.core.data.source.repository.firebase.remoteconfig.FirebaseRemoteConfigRepository
import com.bimabagaskhoro.phincon.core.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class FRCViewModel @Inject constructor(
    private val firebaseRemoteConfigRepository: FirebaseRemoteConfigRepository
) : ViewModel() {

    private val _state = MutableLiveData<Resource<String>>()
    val state: LiveData<Resource<String>> = _state

    init {
        getPaymentMethod()
    }

    private fun getPaymentMethod() {
        firebaseRemoteConfigRepository.getPaymentMethod().onEach { response ->
            when (response) {
                is Resource.Loading -> {
                    _state.value = Resource.Loading()
                }
                is Resource.Success -> {
                    response.data?.let {
                        _state.value = Resource.Success(it)
                    }
                }
                is Resource.Error -> {
                    _state.value = Resource.Error(response.message, response.errorCode, response.errorBody)
                }
                is Resource.Empty ->{
                    _state.value =Resource.Empty()
                }
            }
        }.launchIn(viewModelScope)
    }
}