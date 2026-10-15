package com.app.shoply.ui.products

sealed interface ProductSearchScreenState {

    data object Idle : ProductSearchScreenState

    data class Search(
        val query: String,
        val isClearButtonVisible: Boolean,
        val content: SearchContentState
    ) : ProductSearchScreenState
}

sealed interface SearchContentState {
    data object Initial : SearchContentState

    data object Loading : SearchContentState

    data class Success(val bottomSheet: BottomSheetState) : SearchContentState

    data class Error(val error: SearchError) : SearchContentState
}

sealed interface SearchError {
    data object Empty : SearchError

    data object NoInternet : SearchError

    data object Technical : SearchError
}

sealed interface BottomSheetState {
    data object Hidden : BottomSheetState
    data class Visible(val product: ProductUiModel) : BottomSheetState
}