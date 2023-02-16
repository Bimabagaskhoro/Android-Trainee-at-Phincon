package com.bimabagaskhoro.taskappphincon.data.source.repository.firebase

import com.bimabagaskhoro.taskappphincon.utils.Resource
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigClientException
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class FirebaseRepositoryImpl @Inject constructor(
    private val frc: FirebaseRemoteConfig
) : FirebaseRepository {

    override fun getPaymentMethod(): Flow<Resource<String>> = callbackFlow {
        trySend(Resource.Loading())

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        frc.setConfigSettingsAsync(configSettings)
        frc.fetchAndActivate()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val getRemoteKey = frc.getString("payment_json")
                    trySend(Resource.Success(getRemoteKey))
                } else {
                    close(task.exception ?: Exception("Unknown error occurred"))
                }
            }
            .addOnFailureListener {
                when (it) {
                    is FirebaseNetworkException -> trySend(
                        Resource.Error(
                            true,
                            null,
                            null
                        )
                    )
                    is FirebaseRemoteConfigClientException -> {
                        Resource.Error(true, null, null)
                    }
                    is FirebaseTooManyRequestsException -> {
                        Resource.Error(true, null, null)
                    }
                    is FirebaseRemoteConfigException -> {
                        Resource.Error(true, null, null)
                    }
                    is FirebaseException -> {
                        Resource.Error(true, null, null)
                    }
                }
            }
        awaitClose()


    }

}