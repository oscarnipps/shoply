package com.app.shoply.di

import kotlinx.coroutines.CoroutineDispatcher

interface DispatcherProvider {
    val io : CoroutineDispatcher
    val main : CoroutineDispatcher
}