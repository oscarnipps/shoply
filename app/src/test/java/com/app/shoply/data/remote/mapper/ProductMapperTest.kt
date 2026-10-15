package com.app.shoply.data.remote.mapper

import com.app.shoply.TestData
import com.app.shoply.domain.ProductPage
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class ProductMapperTest {

    private val mapper = ProductMapper()

    @Test
    fun `maps product dto to product item`() {
        assertEquals(TestData.productItem(), mapper.map(TestData.productDto()))
    }

    @Test
    fun `maps response to product page with paging metadata`() {
        val response = TestData.response(
            products = listOf(TestData.productDto(id = 1), TestData.productDto(id = 2)),
            currentPage = 2,
            pageCount = 3,
            totalResults = 70
        )

        val page = mapper.map(response)

        assertEquals(
            ProductPage(
                products = listOf(TestData.productItem(id = 1), TestData.productItem(id = 2)),
                currentPage = 2,
                pageCount = 3
            ),
            page
        )
    }
}