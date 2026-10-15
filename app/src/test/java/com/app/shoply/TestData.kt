package com.app.shoply

import com.app.shoply.data.remote.model.Product
import com.app.shoply.data.remote.model.ProductSearchResponse
import com.app.shoply.data.remote.model.PromoIcon
import com.app.shoply.data.remote.model.ReviewInformation
import com.app.shoply.data.remote.model.ReviewSummary
import com.app.shoply.domain.ProductItem
import com.app.shoply.domain.ProductPage

object TestData {

    fun productDto(
        id: Long = 785359,
        listPriceIncVat: Double? = null,
        promoIcon: PromoIcon? = PromoIcon(text = "middenklasse iPhone", type = "coolblues-choice")
    ) = Product(
        productId = id,
        productName = "Apple iPhone 6 32GB Grijs",
        reviewInformation = ReviewInformation(
            reviews = emptyList(),
            reviewSummary = ReviewSummary(reviewAverage = 9.1, reviewCount = 952)
        ),
        USPs = listOf("32 GB opslagcapaciteit", "4,7 inch Retina HD scherm", "iOS 11"),
        availabilityState = 2,
        salesPriceIncVat = 369.0,
        listPriceIncVat = listPriceIncVat,
        listPriceExVat = null,
        productImage = "https://image.coolblue.nl/300x750/products/818870",
        coolbluesChoiceInformationTitle = "",
        promoIcon = promoIcon,
        nextDayDelivery = true
    )

    fun productItem(
        id: Long = 785359,
    ) = ProductItem(
        productId = id,
        name = "Apple iPhone 6 32GB Grijs",
        reviewAverage = 9.1,
        reviewCount = 952,
        itemSellingPoints = listOf("32 GB opslagcapaciteit", "4,7 inch Retina HD scherm", "iOS 11"),
        availabilityState = 2,
        priceIncludingVat = 369.0,
        image = "https://image.coolblue.nl/300x750/products/818870",
        nextDayDelivery = true
    )

    fun response(
        products: List<Product> = listOf(productDto()),
        currentPage: Int = 1,
        pageCount: Int = 1,
        totalResults: Int = products.size
    ) = ProductSearchResponse(
        products = products,
        currentPage = currentPage,
        pageSize = 24,
        totalResults = totalResults,
        pageCount = pageCount
    )

    fun productPage(page: Int, pageCount: Int, size: Int = 24) = ProductPage(
        products = (1..size).map { productItem(id = page * 100L + it) },
        currentPage = page,
        pageCount = pageCount
    )

    fun emptyProductPage(page: Int, pageCount: Int, size: Int = 24) = ProductPage(
        products = emptyList(),
        currentPage = page,
        pageCount = pageCount
    )
}