package com.app.shoply.domain.common

sealed interface AppResult<T> {
    data class Success<T>(val data: T) : AppResult<T>

    data class Error<T>(val appError: AppError, val errorMessage: String? = "") : AppResult<T>
}