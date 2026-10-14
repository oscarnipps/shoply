package com.app.shoply.di

import android.content.Context
import com.app.shoply.util.AppConnectivityStateManager
import com.app.shoply.util.AppConnectivityStateManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun providesDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
    }

    @Provides
    @Singleton
    fun provideConnectivityStateManager(context : Context): AppConnectivityStateManager {
        return AppConnectivityStateManagerImpl(context)
    }
}