package com.app.shoply.data.remote.model

data class ProductSearchResponse(
    val products: List<Product>,
    val currentPage: Int,
    val pageSize: Int,
    val totalResults: Int,
    val pageCount: Int
)

data class Product(
    val productId: Long,
    val productName: String,
    val reviewInformation: ReviewInformation,
    val USPs: List<String> = emptyList(),
    val availabilityState: Int,
    val salesPriceIncVat: Double,
    val listPriceIncVat: Double? = null,
    val listPriceExVat: Double? = null,
    val productImage: String,
    val coolbluesChoiceInformationTitle: String? = null,
    val promoIcon: PromoIcon? = null,
    val nextDayDelivery: Boolean
)

data class ReviewInformation(
    val reviews: List<Any> = emptyList(),
    val reviewSummary: ReviewSummary
)

data class ReviewSummary(
    val reviewAverage: Double,
    val reviewCount: Int
)

data class PromoIcon(
    val text: String,
    val type: String,
    val imageId: Long? = null
)
