package com.app.shoply.ui.products

import com.app.shoply.domain.ProductItem
import kotlinx.collections.immutable.toImmutableList
import java.text.NumberFormat
import java.util.Locale
import javax.inject.Inject

class ProductUiMapper @Inject constructor() {

    fun map(item: ProductItem): ProductUiModel = ProductUiModel(
        id = item.productId,
        name = item.name,
        imageUrl = item.image,
        price = formatPrice(item.priceIncludingVat),
        rating = ratingFormat.format(item.reviewAverage),
        ratingFraction = (item.reviewAverage / MAX_RATING).toFloat().coerceIn(0f, 1f),
        reviewCount = item.reviewCount,
        sellingPoints = item.itemSellingPoints.toImmutableList(),
        availability = item.availabilityState.toAvailability(),
        nextDayDelivery = item.nextDayDelivery,
    )

    private fun Int.toAvailability(): ProductAvailability = when (this) {
        2 -> ProductAvailability.IN_STOCK
        1 -> ProductAvailability.LOW_STOCK
        0 -> ProductAvailability.OUT_OF_STOCK
        else -> ProductAvailability.UNKNOWN
    }

    private fun formatPrice(price: Double): String =
        if (price % 1.0 == 0.0) "€ ${integerFormat.format(price.toLong())},-"
        else "€ ${decimalFormat.format(price)}"

    private companion object {
        const val MAX_RATING = 10.0

        val DUTCH: Locale = Locale.forLanguageTag("nl-NL")
        val integerFormat: NumberFormat = NumberFormat.getIntegerInstance(DUTCH)
        val decimalFormat: NumberFormat = NumberFormat.getNumberInstance(DUTCH).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }
        val ratingFormat: NumberFormat = NumberFormat.getNumberInstance(DUTCH).apply {
            minimumFractionDigits = 1
            maximumFractionDigits = 1
        }
    }
}