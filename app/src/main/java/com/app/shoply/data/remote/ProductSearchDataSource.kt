package com.app.shoply.data.remote

import com.app.shoply.domain.common.AppResult
import com.app.shoply.domain.ProductPage

interface ProductSearchDataSource {
    suspend fun getProducts(query: String, page: Int): AppResult<ProductPage>
}