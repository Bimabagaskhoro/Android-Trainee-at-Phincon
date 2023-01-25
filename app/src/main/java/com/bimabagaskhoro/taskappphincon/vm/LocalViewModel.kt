package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bimabagaskhoro.taskappphincon.data.source.local.model.cart.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.LocalDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val repository: LocalDataSource
) : ViewModel() {

    fun deleteCart(id: Int) = viewModelScope.launch(Dispatchers.IO) { repository.deleteCart(id) }

    fun insertCart(cartEntity: CartEntity) = viewModelScope.launch(Dispatchers.IO) { repository.saveCart(cartEntity)}

    val getAllCart: LiveData<List<CartEntity>> = repository.getAllCarts().asLiveData()

}