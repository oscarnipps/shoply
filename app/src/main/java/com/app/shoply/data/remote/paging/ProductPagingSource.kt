package com.app.shoply.data.remote.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.app.shoply.data.remote.ProductSearchDataSource
import com.app.shoply.domain.ProductItem
import com.app.shoply.domain.common.AppException
import com.app.shoply.domain.common.AppResult

class ProductPagingSource(
    private val dataSource: ProductSearchDataSource,
    private val query: String
) : PagingSource<Int, ProductItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductItem> {
        val page = params.key ?: STARTING_PAGE

        return when (val result = dataSource.getProducts(query = query, page = page)) {
            is AppResult.Success -> {
                val products = result.data.products

                LoadResult.Page(
                    data = products,
                    prevKey = if (page == STARTING_PAGE) null else page - 1,
                    nextKey = if (products.isEmpty() || page >= result.data.pageCount) null else page + 1
                )
            }

            is AppResult.Error -> LoadResult.Error(
                AppException(result.appError, result.errorMessage)
            )
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ProductItem>): Int? =
        state.anchorPosition?.let { anchor ->
            state.closestPageToPosition(anchor)?.let { page ->
                page.prevKey?.plus(1) ?: page.nextKey?.minus(1)
            }
        }

    companion object {
        const val STARTING_PAGE = 1
        const val PAGE_SIZE = 24
    }
}

