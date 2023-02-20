package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bimabagaskhoro.taskappphincon.data.source.repository.local.LocalDataSource
import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val localDataSource: LocalDataSource
) : ViewModel() {

    /**
     * trolley
     */

    fun getAllProduct(): Flow<List<CartEntity>> {
        return localDataSource.getAllProduct()
    }

    fun getAllCheckedProduct(): Flow<List<CartEntity>> {
        return localDataSource.getAllCheckedProduct()
    }

    fun insertCart(cartDataDomain: CartEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            localDataSource.addProductToTrolley(cartDataDomain)
        }
    }

    fun updateProductData(quantity: Int?, itemTotalPrice: Int?, id: Int?) {
        viewModelScope.launch {
            localDataSource.updateProductData(quantity, itemTotalPrice, id)
        }
    }

    fun updateProductIsCheckedAll(isChecked: Boolean) {
        viewModelScope.launch {
            localDataSource.updateProductIsCheckedAll(isChecked)
        }
    }

    fun updateProductIsCheckedById(isChecked: Boolean, id: Int?) {
        viewModelScope.launch {
            localDataSource.updateProductIsCheckedById(isChecked, id)
        }
    }

    fun deleteProductByIdFromTrolley(id: Int?) {
        viewModelScope.launch {
            localDataSource.deleteProductByIdFromTrolley(id)
        }
    }

    /**
     * notification
     */
    fun getAllNotification(): Flow<List<NotificationEntity>> {
        return localDataSource.getAllNotification()
    }

    fun updateReadNotification(isRead: Boolean, id: Int?) {
        viewModelScope.launch {
            localDataSource.updateReadNotification(isRead, id)
        }
    }

    fun setAllReadNotification(isRead: Boolean) {
        viewModelScope.launch {
            localDataSource.setAllReadNotification(isRead)
        }
    }

    fun updateCheckedNotification(isChecked: Boolean, id: Int?) {
        viewModelScope.launch {
            localDataSource.updateCheckedNotification(isChecked, id)
        }
    }

    fun setAllUncheckedNotification(isChecked: Boolean = false) {
        viewModelScope.launch {
            localDataSource.setAllUncheckedNotification(isChecked)
        }
    }

    fun deleteNotification(isChecked: Boolean) {
        viewModelScope.launch {
            localDataSource.deleteNotification(isChecked)
        }
    }
}