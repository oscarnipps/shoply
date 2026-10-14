package com.app.shoply.data.repo

import androidx.paging.PagingData
import com.app.shoply.domain.ProductItem
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun searchProducts(query: String): Flow<PagingData<ProductItem>>
}