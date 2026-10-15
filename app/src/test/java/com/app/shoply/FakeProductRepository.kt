package com.app.shoply

import androidx.paging.PagingData
import com.app.shoply.data.repo.ProductRepository
import com.app.shoply.domain.ProductItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeProductRepository : ProductRepository {

    var items: List<ProductItem> = emptyList()

    val queries = mutableListOf<String>()

    override fun searchProducts(query: String): Flow<PagingData<ProductItem>> {
        queries += query
        return flowOf(PagingData.from(items))
    }
}