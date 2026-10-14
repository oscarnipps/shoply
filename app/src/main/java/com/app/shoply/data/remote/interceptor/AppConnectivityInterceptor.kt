package com.app.shoply.data.remote.interceptor

import com.app.shoply.util.AppConnectivityStateManager
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

class NoInternetException : IOException("No internet connection")

class AppConnectivityInterceptor @Inject constructor(
    private val connectivityStateManager: AppConnectivityStateManager,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!connectivityStateManager.isConnected()) {
            throw NoInternetException()
        }
        return chain.proceed(chain.request())
    }
}