package com.app.shoply.di


import com.app.shoply.data.remote.interceptor.AppConnectivityInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Singleton

const val BASE_URL = "https://bdk0sta2n0.execute-api.eu-west-1.amazonaws.com/mobile-assignment/"

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideOkhttpClient(
        connectivityInterceptor: AppConnectivityInterceptor,
    ): OkHttpClient {
        val logging = HttpLoggingInterceptor()
            .apply { level = HttpLoggingInterceptor.Level.BODY }

        return OkHttpClient.Builder()
            .addInterceptor(connectivityInterceptor)
            .addInterceptor(logging)
            .build()
    }
}