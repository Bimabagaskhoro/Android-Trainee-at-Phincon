package com.bimabagaskhoro.taskappphincon.vm

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.bimabagaskhoro.taskappphincon.data.source.repository.product.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    fun getProduct(
        search: String? = null
    ) = productRepository.getDataProduct(search).asLiveData()

    fun getFavProduct(
        userId: Int,
        search: String? = null
    ) = productRepository.getDataFavProduct(userId, search).asLiveData()
}