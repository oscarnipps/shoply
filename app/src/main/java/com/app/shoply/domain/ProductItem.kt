package com.app.shoply.domain

data class ProductItem(
    val productId: Long,
    val name: String,
    val reviewAverage: Double,
    val reviewCount: Int,
    val itemSellingPoints: List<String>,
    val availabilityState: Int,
    val priceIncludingVat: Double,
    val image: String,
    val nextDayDelivery: Boolean
)