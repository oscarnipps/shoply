package com.app.shoply.data.remote.mapper

import com.app.shoply.data.remote.model.Product
import com.app.shoply.data.remote.model.ProductSearchResponse
import com.app.shoply.domain.ProductItem
import com.app.shoply.domain.ProductPage
import javax.inject.Inject

class ProductMapper @Inject constructor() {

    fun map(response: ProductSearchResponse): ProductPage = ProductPage(
        products = response.products.map(::map),
        currentPage = response.currentPage,
        pageCount = response.pageCount
    )

    fun map(product: Product): ProductItem = ProductItem(
        productId = product.productId,
        name = product.productName,
        reviewAverage = product.reviewInformation.reviewSummary.reviewAverage,
        reviewCount = product.reviewInformation.reviewSummary.reviewCount,
        itemSellingPoints = product.USPs,
        availabilityState = product.availabilityState,
        priceIncludingVat = product.salesPriceIncVat,
        image = product.productImage,
        nextDayDelivery = product.nextDayDelivery
    )
}