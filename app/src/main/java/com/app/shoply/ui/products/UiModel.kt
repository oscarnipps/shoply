package com.app.shoply.ui.products

import androidx.paging.LoadState
import kotlinx.collections.immutable.ImmutableList

data class ProductUiModel(
    val id: Long,
    val name: String,
    val imageUrl: String,
    val price: String,
    val rating: String,
    val ratingFraction: Float,
    val reviewCount: Int,
    val sellingPoints: ImmutableList<String>,
    val availability: ProductAvailability,
    val nextDayDelivery: Boolean
)

data class SearchInputs(
    val isSearchActive: Boolean = false,
    val query: String = "",
    val selectedProduct: ProductUiModel? = null,
    val refresh: LoadState? = null,
    val itemCount: Int = 0
)