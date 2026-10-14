package com.app.shoply.data.remote.paging

import org.junit.jupiter.api.Assertions.*

import androidx.paging.PagingConfig
import androidx.paging.PagingSource.LoadResult
import androidx.paging.testing.TestPager
import com.app.shoply.TestData
import com.app.shoply.data.remote.FakeProductSearchDataSource
import com.app.shoply.domain.common.AppError
import com.app.shoply.domain.common.AppException
import com.app.shoply.domain.common.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ProductPagingSourceTest {

    private val dataSource = FakeProductSearchDataSource()
    private val pagingSource = ProductPagingSource(dataSource, query = "apple")
    private val pager = TestPager(
        config = PagingConfig(pageSize = 24, initialLoadSize = 24, enablePlaceholders = false),
        pagingSource = pagingSource
    )

    @Test
    fun `refresh loads first page with next key`() = runTest {
        val firstPage = TestData.productPage(page = 1, pageCount = 3)
        dataSource.results[1] = AppResult.Success(firstPage)

        val result = pager.refresh() as LoadResult.Page

        assertEquals(firstPage.products, result.data)
        assertNull(result.prevKey)
        assertEquals(2, result.nextKey)
        assertEquals(listOf("apple" to 1), dataSource.requests)
    }

    @Test
    fun `append loads next page`() = runTest {
        dataSource.results[1] = AppResult.Success(TestData.productPage(page = 1, pageCount = 3))
        dataSource.results[2] = AppResult.Success(TestData.productPage(page = 2, pageCount = 3))
        pager.refresh()

        val result = pager.append() as LoadResult.Page

        assertEquals(1, result.prevKey)
        assertEquals(3, result.nextKey)
        assertEquals(listOf(1, 2), dataSource.requests.map { it.second })
    }

    @Test
    fun `last page has no next key`() = runTest {
        dataSource.results[1] = AppResult.Success(TestData.productPage(page = 1, pageCount = 1))

        val result = pager.refresh() as LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `empty page ends pagination`() = runTest {
        dataSource.results[1] = AppResult.Success(TestData.productPage(page = 1, pageCount = 3, size = 0))

        val result = pager.refresh() as LoadResult.Page

        assertNull(result.nextKey)
    }

    @Test
    fun `error result is propagated as AppException`() = runTest {
        dataSource.results[1] = AppResult.Error(AppError.NO_INTERNET, "offline")

        val result = pager.refresh() as LoadResult.Error

        val exception = result.throwable as AppException
        assertEquals(AppError.NO_INTERNET, exception.appError)
        assertEquals("offline", exception.message)
    }

    @Test
    fun `refresh key is the page closest to the anchor position`() = runTest {
        dataSource.results[1] = AppResult.Success(TestData.productPage(page = 1, pageCount = 3))
        dataSource.results[2] = AppResult.Success(TestData.productPage(page = 2, pageCount = 3))
        pager.refresh()
        pager.append()

        val state = pager.getPagingState(anchorPosition = 30)

        assertEquals(2, pagingSource.getRefreshKey(state))
    }
}