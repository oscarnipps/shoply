package com.app.shoply.data.repo

import androidx.paging.testing.asSnapshot
import com.app.shoply.TestData
import com.app.shoply.data.remote.FakeProductSearchDataSource
import com.app.shoply.domain.common.AppResult
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ProductRepositoryImplTest {

    private val dataSource = FakeProductSearchDataSource()
    private val repository = ProductRepositoryImpl(dataSource)

    @Before
    fun setUp() {
        dataSource.results[1] = AppResult.Success(TestData.productPage(page = 1, pageCount = 2))
        dataSource.results[2] = AppResult.Success(TestData.productPage(page = 2, pageCount = 2, size = 2))
    }

    @Test
    fun `loads pages in order for the given query until the last page`() = runTest {
        val items = repository.searchProducts("apple").asSnapshot()

        val expected = TestData.productPage(page = 1, pageCount = 2).products +
                TestData.productPage(page = 2, pageCount = 2, size = 2).products

        assertEquals(expected, items)
        assertEquals(listOf("apple" to 1, "apple" to 2), dataSource.requests)
    }
}