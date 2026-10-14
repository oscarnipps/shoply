package com.app.shoply.domain.common

class AppException(
    val appError: AppError,
    val exceptionMsg: String? = null
) : Exception(exceptionMsg)