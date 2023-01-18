package com.bimabagaskhoro.taskappphincon.data.source.repository.product

import com.bimabagaskhoro.taskappphincon.data.source.local.db.ProductDatabase
import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiPaging
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiPaging: ApiPaging,
    private val feedDB: ProductDatabase
) : ProductRepository {

}