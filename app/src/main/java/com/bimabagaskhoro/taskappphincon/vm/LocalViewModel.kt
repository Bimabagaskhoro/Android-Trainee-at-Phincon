package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.bimabagaskhoro.taskappphincon.data.source.local.model.CartEntity
import com.bimabagaskhoro.taskappphincon.data.source.local.LocalDataSource
import com.bimabagaskhoro.taskappphincon.data.source.local.model.NotificationEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocalViewModel @Inject constructor(
    private val repository: LocalDataSource
) : ViewModel() {

    fun deleteCart(id: Int) = viewModelScope.launch(Dispatchers.IO) { repository.deleteCart(id) }

    fun insertCart(cartEntity: CartEntity) =
        viewModelScope.launch(Dispatchers.IO) { repository.saveCart(cartEntity) }

    val getAllCart: LiveData<List<CartEntity>> = repository.getAllCarts().asLiveData()
    val countAllCart: Int = repository.countItems()

    fun updateQuantity(quantity: Int, id: Int, newTotalPrice: Int): Int =
        repository.updateQuantity(quantity, id, newTotalPrice)

    fun updatePriceCard(harga: Int, id: Int): Int = repository.updatePriceCard(harga, id)

//    fun getTrolleyChecked(): List<DataTrolley>? {
//        return repository.getCheckedTrolley()
//    }

    fun checkAll(state: Int): Int = repository.checkALl(state)

    fun updateCheck(id: Int, state: Int): Int = repository.updateCheck(id, state)

    fun getTotalPrice(): Int {
        return repository.getTotalHarga()
    }

    fun deleteTrolleyChecked(): Int? {
        return repository.deleteCheckedTrolley()
    }

    val getTrolleyChecked: LiveData<List<CartEntity>> =
        repository.getAllCheckedProductFromTrolly().asLiveData()

    /**
     * notification
     */

    val getAllNotification: LiveData<List<NotificationEntity>> =
        repository.getAllNotification().asLiveData()

    val countAllNotification: Int = repository.countItemNotification()

    fun getTotalNotification(): Int {
        return repository.getTotalNotification()
    }

    fun getTotalIsReadNotification(): Int {
        return repository.getTotalIsReadNotification()
    }

    fun updateTotalNotification(totalNotification: Int, id: Int): Int =
        repository.updateTotalNotification(totalNotification, id)

    fun isRead(state: Int, id: Int): Int = repository.isRead(state, id)

    fun deleteNotification(): Int? {
        return repository.deleteNotification()
    }

    val getAllCheckedNotification: LiveData<List<NotificationEntity>> =
        repository.getAllCheckedNotification().asLiveData()

    fun updateCheckNotification(id: Int, state: Int): Int = repository.updateCheckNotification(id, state)

    fun checkAllNotification(state: Int): Int = repository.checkAllNotification(state)

    fun viewCheckBoxAnimation(state: Int): Int = repository.viewCheckBoxAnimation(state)

    fun deleteNotifications(id: Int) = viewModelScope.launch(Dispatchers.IO) { repository.deleteNotification(id) }


}