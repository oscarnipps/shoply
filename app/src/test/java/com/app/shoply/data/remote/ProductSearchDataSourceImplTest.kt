package com.app.shoply.data.remote

import com.app.shoply.TestData
import com.app.shoply.data.remote.api.ProductSearchService
import com.app.shoply.data.remote.interceptor.NoInternetException
import com.app.shoply.data.remote.mapper.ProductMapper
import com.app.shoply.domain.common.AppError
import com.app.shoply.domain.common.AppResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

class ProductSearchDataSourceImplTest {

    private val service: ProductSearchService = mockk()
    private val mapper = ProductMapper()
    private val dataSource = ProductSearchDataSourceImpl(service, mapper)

    @Test
    fun `returns mapped page on success`() = runTest {
        val response = TestData.response(pageCount = 3, totalResults = 70)
        coEvery { service.searchProducts("apple", 2) } returns response

        val result = dataSource.getProducts(query = "apple", page = 2)

        assertEquals(AppResult.Success(mapper.map(response)), result)
        coVerify(exactly = 1) { service.searchProducts("apple", 2) }
    }

    @Test
    fun `returns NO_DATA when search has no results`() = runTest {
        coEvery { service.searchProducts(any(), any()) } returns
                TestData.response(products = emptyList(), totalResults = 0)

        val result = dataSource.getProducts(query = "xyz", page = 1)

        assertEquals(result,  AppResult.Success(TestData.emptyProductPage(page = 1, pageCount = 1)))
    }

    @Test
    fun `returns NO_INTERNET on IOException`() = runTest {
        coEvery { service.searchProducts(any(), any()) } throws NoInternetException()

        val result = dataSource.getProducts(query = "apple", page = 1)

        assertEquals(AppError.NO_INTERNET, (result as AppResult.Error).appError)
    }

    @Test
    fun `returns TECHNICAL on HttpException`() = runTest {
        coEvery { service.searchProducts(any(), any()) } throws
                HttpException(Response.error<Any>(500, "".toResponseBody(null)))

        val result = dataSource.getProducts(query = "apple", page = 1)

        assertEquals(AppError.TECHNICAL, (result as AppResult.Error).appError)
    }
}