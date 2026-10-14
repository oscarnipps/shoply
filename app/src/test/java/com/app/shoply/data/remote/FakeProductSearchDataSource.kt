package com.app.shoply.data.remote

import com.app.shoply.domain.ProductPage
import com.app.shoply.domain.common.AppError
import com.app.shoply.domain.common.AppResult

class FakeProductSearchDataSource : ProductSearchDataSource {
    val results = mutableMapOf<Int, AppResult<ProductPage>>()

    val requests = mutableListOf<Pair<String, Int>>()

    override suspend fun getProducts(query: String, page: Int): AppResult<ProductPage> {
        requests += query to page
        return results[page] ?: AppResult.Error(AppError.TECHNICAL, "No result for page $page")
    }
}