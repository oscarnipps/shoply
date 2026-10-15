package com.app.shoply.ui.products

import androidx.paging.LoadState

sealed interface ProductSearchScreenEvent {
    data object SearchModeActive : ProductSearchScreenEvent

    data class QueryChanged(val query: String) : ProductSearchScreenEvent

    data class ProductClicked(val product: ProductUiModel) : ProductSearchScreenEvent

    data object BottomSheetDismissed : ProductSearchScreenEvent

    data object CancelClicked : ProductSearchScreenEvent

    data object ClearSearchClicked : ProductSearchScreenEvent

    data object RetryClicked : ProductSearchScreenEvent

    data class PagingLoadStateChanged(val refresh: LoadState, val itemCount: Int) : ProductSearchScreenEvent
}