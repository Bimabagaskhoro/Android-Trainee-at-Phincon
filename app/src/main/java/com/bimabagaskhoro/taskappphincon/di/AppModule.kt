package com.bimabagaskhoro.taskappphincon.di

import android.content.Context
import com.bimabagaskhoro.taskappphincon.BuildConfig
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.data.source.network.ApiService
import com.bimabagaskhoro.taskappphincon.data.source.repository.AuthRepository
import com.bimabagaskhoro.taskappphincon.data.source.repository.AuthRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        val loggingInterceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        if (!BuildConfig.DEBUG) {
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE)
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl("http://172.17.20.201/training_android/public/api/ecommerce/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService) : AuthRepository {
        return AuthRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): AuthPreferences =
        AuthPreferences(context)

}