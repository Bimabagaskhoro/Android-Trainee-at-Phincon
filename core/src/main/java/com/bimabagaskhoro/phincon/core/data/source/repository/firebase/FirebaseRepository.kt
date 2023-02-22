package com.bimabagaskhoro.phincon.core.data.source.repository.firebase

import com.bimabagaskhoro.phincon.core.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    fun getPaymentMethod(): Flow<Resource<String>>
}