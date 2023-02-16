package com.bimabagaskhoro.taskappphincon.data.source.repository.firebase

import com.bimabagaskhoro.taskappphincon.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    fun getPaymentMethod(): Flow<Resource<String>>
}