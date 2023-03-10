package com.bimabagaskhoro.phincon.core.di

import android.content.Context
import androidx.room.Room
import com.bimabagaskhoro.phincon.core.BuildConfig
import com.bimabagaskhoro.phincon.core.data.pref.AuthPreferences
import com.bimabagaskhoro.phincon.core.data.source.local.db.AppRoomDatabase
import com.bimabagaskhoro.phincon.core.data.source.local.db.dao.CartDao
import com.bimabagaskhoro.phincon.core.data.source.local.db.dao.NotificationDao
import com.bimabagaskhoro.phincon.core.data.source.remote.network.ApiService
import com.bimabagaskhoro.phincon.core.data.source.repository.firebase.analytic.FirebaseAnalyticRepository
import com.bimabagaskhoro.phincon.core.data.source.repository.firebase.analytic.FirebaseAnalyticRepositoryImpl
import com.bimabagaskhoro.phincon.core.data.source.repository.firebase.remoteconfig.FirebaseRemoteConfigRepository
import com.bimabagaskhoro.phincon.core.data.source.repository.firebase.remoteconfig.FirebaseRemoteConfigRepositoryImpl
import com.bimabagaskhoro.phincon.core.data.source.repository.local.LocalDataSource
import com.bimabagaskhoro.phincon.core.data.source.repository.local.LocalDataSourceImpl
import com.bimabagaskhoro.phincon.core.data.source.repository.remote.RemoteRepository
import com.bimabagaskhoro.phincon.core.data.source.repository.remote.RemoteRepositoryImpl
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.BASE_URL
import com.bimabagaskhoro.phincon.core.utils.Constant.Companion.CART_DATABASE
import com.bimabagaskhoro.phincon.core.utils.interceptor.*
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
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
        headerInterceptor: HeaderInterceptor,
        noInternetInterceptor: NoInternetInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .addInterceptor(headerInterceptor) //header
            .addInterceptor(authBadResponse) // 401 bad response
            .addInterceptor(noInternetInterceptor) // time Out
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
    ): AuthBadResponse =
        AuthBadResponse(authPreferences, context)

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
    fun provideNoInternetInterceptor(connectionManager: ConnectionManager
    ): NoInternetInterceptor =
        NoInternetInterceptor(connectionManager)

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
    ): RemoteRepository {
        return RemoteRepositoryImpl(apiService)
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
        context, AppRoomDatabase::class.java, CART_DATABASE
    )
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Provides
    @Singleton
    fun provideCartDao(db: AppRoomDatabase) = db.cartDao()


    @Provides
    @Singleton
    fun provideNotificationDao(db: AppRoomDatabase) = db.notificationDao()

    @Singleton
    @Provides
    fun providesLocalDataSource(
        cartDao: CartDao,
        notificationDao: NotificationDao
    ): LocalDataSource {
        return LocalDataSourceImpl(cartDao, notificationDao)
    }

    /**
     * firebase config
     */

    @Provides
    @Singleton
    fun providesFirebaseRemoteConfig(): FirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    @Provides
    @Singleton
    fun providesFirebaseRepository(
        fcm: FirebaseRemoteConfig
    ): FirebaseRemoteConfigRepository = FirebaseRemoteConfigRepositoryImpl(fcm)

    /**
     * firebase analytic
     */

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics {
        return FirebaseAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun providesFirebaseAnalyticRepository(
        fga: FirebaseAnalytics
    ): FirebaseAnalyticRepository = FirebaseAnalyticRepositoryImpl(fga)

}