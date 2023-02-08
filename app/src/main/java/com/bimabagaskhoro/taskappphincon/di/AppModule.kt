package com.bimabagaskhoro.taskappphincon.di

import android.content.Context
import androidx.room.Room
import com.bimabagaskhoro.taskappphincon.BuildConfig
import com.bimabagaskhoro.taskappphincon.data.pref.AuthPreferences
import com.bimabagaskhoro.taskappphincon.data.source.local.db.notification.NotificationDao
import com.bimabagaskhoro.taskappphincon.data.source.local.db.notification.NotificationDatabase
import com.bimabagaskhoro.taskappphincon.data.source.local.db.trolley.CartDatabase
import com.bimabagaskhoro.taskappphincon.data.source.remote.network.ApiService
import com.bimabagaskhoro.taskappphincon.data.source.repository.AuthRepository
import com.bimabagaskhoro.taskappphincon.data.source.repository.AuthRepositoryImpl
import com.bimabagaskhoro.taskappphincon.fcm.FirebaseNotification
import com.bimabagaskhoro.taskappphincon.utils.AuthAuthenticator
import com.bimabagaskhoro.taskappphincon.utils.AuthBadResponse
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.BASE_URL
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.CART_DATABASE
import com.bimabagaskhoro.taskappphincon.utils.Constant.Companion.NOTIFICATION_DATABASE
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

    @Provides
    @Singleton
    fun provideUserRepository(
        apiService: ApiService
    ): AuthRepository {
        return AuthRepositoryImpl(apiService)
    }

    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): AuthPreferences =
        AuthPreferences(context)
    /**
     * trolley database
     */
    @Provides
    @Singleton
    fun provideCartDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, CartDatabase::class.java, CART_DATABASE
    )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideCartDao(db: CartDatabase) = db.cartDao()


    /**
     * notification database
     */
    @Provides
    @Singleton
    fun provideNotificationDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
        context, NotificationDatabase::class.java, NOTIFICATION_DATABASE
    )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideNotificationDao(db: NotificationDatabase) = db.notificationDao()
}