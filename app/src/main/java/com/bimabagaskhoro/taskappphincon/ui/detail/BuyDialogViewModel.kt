package com.bimabagaskhoro.taskappphincon.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

class BuyDialogViewModel: ViewModel() {
    private var _quantity = MutableLiveData<Int>()
    val quantity: LiveData<Int> = _quantity

    private var _price = MutableLiveData<Int>()
    val price: LiveData<Int> = _price

    init {
        _quantity.value = 1
    }

    fun addQuantity(stock: Int?) {
        if (_quantity.value!! < stock!!) {
            _quantity.value = _quantity.value?.plus(1)
        }
    }

    fun minQuantity() {
        if (quantity.value == 1) {
            _quantity.value = 1
        } else {
            _quantity.value = _quantity.value?.minus(1)
        }
    }
}