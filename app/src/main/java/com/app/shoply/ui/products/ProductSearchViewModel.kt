@file:OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)

package com.app.shoply.ui.products

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.app.shoply.data.repo.ProductRepository
import com.app.shoply.domain.common.AppError
import com.app.shoply.domain.common.AppException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.updateAndGet
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class ProductSearchViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val uiMapper: ProductUiMapper,
) : ViewModel() {

    private val inputs = MutableStateFlow(SearchInputs())

    private val _screenState = MutableStateFlow(inputs.value.toScreenState())
    val screenState: StateFlow<ProductSearchScreenState> = _screenState.asStateFlow()

    private val retryTrigger = MutableStateFlow(0)

    val products: Flow<PagingData<ProductUiModel>> =
        combine(
            inputs
                .map { it.query.trim() }
                .distinctUntilChanged()
                .debounce(SEARCH_DEBOUNCE_MS.milliseconds)
                .filter { it.isNotEmpty() }
                .distinctUntilChanged(),
            retryTrigger
        ) { query, _ -> query }
            .flatMapLatest { query ->
                repository.searchProducts(query).map {
                    pagingData -> pagingData.map(uiMapper::map)
                }
            }
            .cachedIn(viewModelScope)

    fun onEvent(event: ProductSearchScreenEvent) {
        when (event) {
            ProductSearchScreenEvent.SearchModeActive ->
                updateInputs { copy(isSearchActive = true) }

            is ProductSearchScreenEvent.QueryChanged ->
                updateInputs { copy(isSearchActive = true, query = event.query) }

            is ProductSearchScreenEvent.ProductClicked ->
                updateInputs { copy(selectedProduct = event.product) }

            ProductSearchScreenEvent.BottomSheetDismissed ->
                updateInputs { copy(selectedProduct = null) }

            ProductSearchScreenEvent.ClearSearchClicked ->
                updateInputs { copy(query = "", selectedProduct = null) }

            ProductSearchScreenEvent.CancelClicked ->
                updateInputs { copy(isSearchActive = false, query = "", selectedProduct = null) }

            ProductSearchScreenEvent.RetryClicked ->
                retryTrigger.update { it + 1 }

            is ProductSearchScreenEvent.PagingLoadStateChanged ->
                updateInputs { copy(refresh = event.refresh, itemCount = event.itemCount) }
        }
    }

    private fun updateInputs(transform: SearchInputs.() -> SearchInputs) {
        val updated = inputs.updateAndGet { it.transform() }
        _screenState.value = updated.toScreenState()
    }

    private fun SearchInputs.toScreenState(): ProductSearchScreenState =
        buildScreenState(isSearchActive, query, selectedProduct, refresh, itemCount)

    internal fun buildScreenState(
        isSearchActive: Boolean,
        query: String,
        selectedProduct: ProductUiModel?,
        refresh: LoadState?,
        itemCount: Int
    ): ProductSearchScreenState {
        if (!isSearchActive) return ProductSearchScreenState.Idle

        return ProductSearchScreenState.Search(
            query = query,
            isClearButtonVisible = query.isNotEmpty(),
            content = buildContentState(query, selectedProduct, refresh, itemCount)
        )
    }

    private fun LoadState.Error.toSearchContentStateError(): SearchContentState {
        val appError = (this.error as AppException).appError

        return SearchContentState.Error(
            when (appError) {
                AppError.NO_INTERNET -> SearchError.NoInternet

                AppError.NO_DATA -> SearchError.Empty

                else -> SearchError.Technical
            }
        )
    }

    private fun buildContentState(
        query: String,
        selectedProduct: ProductUiModel?,
        refresh: LoadState?,
        itemCount: Int
    ): SearchContentState {
        if (query.isBlank()) return SearchContentState.Initial

        return when (refresh) {
            null, is LoadState.Loading -> SearchContentState.Loading

            is LoadState.Error -> refresh.toSearchContentStateError()

            is LoadState.NotLoading ->
                if (itemCount == 0) {
                    SearchContentState.Error(SearchError.Empty)
                } else {
                    SearchContentState.Success(
                        bottomSheet = selectedProduct?.let { BottomSheetState.Visible(it) } ?: BottomSheetState.Hidden
                    )
                }
        }
    }

    private companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
    }
}

