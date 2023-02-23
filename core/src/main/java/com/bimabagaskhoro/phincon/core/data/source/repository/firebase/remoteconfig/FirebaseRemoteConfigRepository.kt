package com.bimabagaskhoro.phincon.core.data.source.repository.firebase.remoteconfig

import com.bimabagaskhoro.phincon.core.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FirebaseRemoteConfigRepository {
    fun getPaymentMethod(): Flow<Resource<String>>
}