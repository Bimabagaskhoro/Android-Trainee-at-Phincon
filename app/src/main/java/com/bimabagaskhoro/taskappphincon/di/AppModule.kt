package com.bimabagaskhoro.taskappphincon.di

import android.content.Context
import com.bimabagaskhoro.taskappphincon.BuildConfig
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.data.source.local.db.ProductDatabase
import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiPaging
import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiService
import com.bimabagaskhoro.taskappphincon.data.source.repository.auth.AuthRepository
import com.bimabagaskhoro.taskappphincon.data.source.repository.auth.AuthRepositoryImpl
import com.bimabagaskhoro.taskappphincon.data.source.repository.product.ProductRepository
import com.bimabagaskhoro.taskappphincon.data.source.repository.product.ProductRepositoryImpl
import com.bimabagaskhoro.taskappphincon.utils.AuthAuthenticator
import com.bimabagaskhoro.taskappphincon.utils.AuthBadResponse
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.BASE_URL
import com.bimabagaskhoro.taskappphincon.utils.HeaderInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun httpLoggingInterceptor(): HttpLoggingInterceptor {
        return if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        } else {
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.NONE)
        }
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        authBadResponse: AuthBadResponse,
        authAuthenticator: AuthAuthenticator,
        headerInterceptor: HeaderInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(headerInterceptor) //header
            .addInterceptor(authBadResponse) // 401 bad response
            .authenticator(authAuthenticator) // get refresh token
            .readTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

    }

    @Singleton
    @Provides
    fun provideAuthInterceptor(
        authPreferences: AuthPreferences,
        @ApplicationContext context: Context
    ): AuthBadResponse = AuthBadResponse(authPreferences, context)

    @Singleton
    @Provides
    fun provideAuthAuthenticator(authPreferences: AuthPreferences): AuthAuthenticator =
        AuthAuthenticator(authPreferences)

    @Singleton
    @Provides
    fun provideHeaderInterceptor(authPreferences: AuthPreferences): HeaderInterceptor =
        HeaderInterceptor(authPreferences)

    @Singleton
    @Provides
    fun provideConverterFactory(): MoshiConverterFactory {
        return MoshiConverterFactory.create()
    }

    @Singleton
    @Provides
    fun provideRetrofitInstance(
        okHttpClient: OkHttpClient,
        moshiConverterFactory: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(moshiConverterFactory)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideApiPaging(retrofit: Retrofit): ApiPaging {
        return retrofit.create(ApiPaging::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(apiService: ApiService): AuthRepository {
        return AuthRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideProductRepository(
        apiPaging: ApiPaging,
        //database: ProductDatabase
    ): ProductRepository {
        return ProductRepositoryImpl(apiPaging
            //, database
        )
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): AuthPreferences =
        AuthPreferences(context)

}