package com.app.shoply.data.repo

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.app.shoply.data.remote.ProductSearchDataSource
import com.app.shoply.data.remote.paging.ProductPagingSource
import com.app.shoply.domain.ProductItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val dataSource: ProductSearchDataSource
) : ProductRepository {

    override fun searchProducts(query: String): Flow<PagingData<ProductItem>> =
        Pager(
            config = PagingConfig(
                pageSize = ProductPagingSource.PAGE_SIZE,
                initialLoadSize = ProductPagingSource.PAGE_SIZE,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { ProductPagingSource(dataSource, query) }
        ).flow
}