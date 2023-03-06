package com.bimabagaskhoro.taskappphincon.di

import com.bimabagaskhoro.phincon.router.ActivityRouter
import com.bimabagaskhoro.taskappphincon.router.ActivityRouterImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class RouterModule {
    @Singleton
    @Provides
    fun providesRouter(): ActivityRouter {
        return ActivityRouterImpl()
    }
}