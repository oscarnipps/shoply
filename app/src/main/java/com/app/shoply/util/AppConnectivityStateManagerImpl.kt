package com.app.shoply.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class AppConnectivityStateManagerImpl @Inject constructor(@ApplicationContext val context: Context) :
    AppConnectivityStateManager {

    override fun isConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        return capabilities?.hasCapability(NET_CAPABILITY_INTERNET) == true
    }
}