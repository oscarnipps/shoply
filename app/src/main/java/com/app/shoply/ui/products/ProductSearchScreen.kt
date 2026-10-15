package com.app.shoply.ui.products

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.app.shoply.ui.components.EmptyContent
import com.app.shoply.ui.components.InitialContent
import com.app.shoply.ui.components.LoadingShimmerContent
import com.app.shoply.ui.components.NoInternetContent
import com.app.shoply.ui.components.ProductBottomSheet
import com.app.shoply.ui.components.ProductList
import com.app.shoply.ui.components.ProductSearchBar
import com.app.shoply.ui.components.TechnicalErrorContent
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf


@Composable
fun ProductSearchScreen(
    modifier: Modifier = Modifier,
    viewModel: ProductSearchViewModel = hiltViewModel(),
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    val products = viewModel.products.collectAsLazyPagingItems()

    val refresh = products.loadState.refresh

    val itemCount = products.itemCount

    LaunchedEffect(refresh, itemCount) {
        viewModel.onEvent(ProductSearchScreenEvent.PagingLoadStateChanged(refresh, itemCount))
    }

    ProductSearchContent(
        state = state,
        products = products,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}

@Composable
fun ProductSearchContent(
    state: ProductSearchScreenState,
    products: LazyPagingItems<ProductUiModel>,
    onEvent: (ProductSearchScreenEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val search = state as? ProductSearchScreenState.Search

    val cancelSearch: () -> Unit = {
        focusManager.clearFocus()
        onEvent(ProductSearchScreenEvent.CancelClicked)
    }

    BackHandler(enabled = search != null, onBack = cancelSearch)

    Scaffold(
        modifier = modifier,
        topBar = {
            ProductSearchBar(
                query = search?.query.orEmpty(),
                isClearButtonVisible = search?.isClearButtonVisible == true,
                isCancelButtonVisible = search != null,
                onQueryChange = { onEvent(ProductSearchScreenEvent.QueryChanged(it)) },
                onFocused = { onEvent(ProductSearchScreenEvent.SearchModeActive) },
                onClearClick = { onEvent(ProductSearchScreenEvent.ClearSearchClicked) },
                onCancelClick = cancelSearch,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding),
        ) {
            when (state) {
                ProductSearchScreenState.Idle -> InitialContent()

                is ProductSearchScreenState.Search -> SearchContent(
                    state = state,
                    products = products,
                    onEvent = onEvent,
                    onProductClick = { product ->
                        focusManager.clearFocus()
                        onEvent(ProductSearchScreenEvent.ProductClicked(product))
                    },
                )
            }
        }
    }
}

@Composable
private fun SearchContent(
    state: ProductSearchScreenState.Search,
    products: LazyPagingItems<ProductUiModel>,
    onEvent: (ProductSearchScreenEvent) -> Unit,
    onProductClick: (ProductUiModel) -> Unit,
) {
    when (val content = state.content) {
        SearchContentState.Initial -> InitialContent()

        SearchContentState.Loading -> LoadingShimmerContent()

        is SearchContentState.Success -> {
            ProductList(
                query = state.query,
                products = products,
                onProductClick = onProductClick,
                onLoadMoreRetry = products::retry,
            )

            val bottomSheet = content.bottomSheet

            if (bottomSheet is BottomSheetState.Visible) {
                ProductBottomSheet(
                    product = bottomSheet.product,
                    onDismiss = { onEvent(ProductSearchScreenEvent.BottomSheetDismissed) },
                )
            }
        }

        is SearchContentState.Error -> when (content.error) {
            SearchError.Empty -> EmptyContent(
                query = state.query,
                onClearSearchClick = { onEvent(ProductSearchScreenEvent.ClearSearchClicked) },
            )

            SearchError.NoInternet -> NoInternetContent(
                onRetryClick = { onEvent(ProductSearchScreenEvent.RetryClicked) },
            )

            SearchError.Technical -> TechnicalErrorContent(
                onRetryClick = { onEvent(ProductSearchScreenEvent.RetryClicked) },
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun ResultsPreview() {
    val append = LoadState.NotLoading(endOfPaginationReached = true)
    val products = listOf(
        ProductUiModel(
            id = 648317,
            name = "Thanos Gold Infinity Ring",
            imageUrl = "https://image.coolblue.nl/300x750/products/1014060",
            price = "€ 509,-",
            rating = "9,2",
            ratingFraction = 0.92f,
            reviewCount = 1273,
            sellingPoints = persistentListOf("Long life", "Time travel"),
            availability = ProductAvailability.IN_STOCK,
            nextDayDelivery = true,
        ),
        ProductUiModel(
            id = 64831,
            name = "Jedi Green Saber",
            imageUrl = "https://image.coolblue.nl/300x750/products/1014060",
            price = "€ 509,-",
            rating = "9,2",
            ratingFraction = 0.92f,
            reviewCount = 1273,
            sellingPoints = persistentListOf("Quick draw", "very useful for droids"),
            availability = ProductAvailability.LOW_STOCK,
            nextDayDelivery = true,
        ),
        ProductUiModel(
            id = 4831,
            name = "Mandalorian Helmet",
            imageUrl = "https://image.coolblue.nl/300x750/products/1014060",
            price = "€ 509,-",
            rating = "9,2",
            ratingFraction = 0.92f,
            reviewCount = 1273,
            sellingPoints = persistentListOf("Stronger than hulk", "dust free"),
            availability = ProductAvailability.OUT_OF_STOCK,
            nextDayDelivery = true,
        ),
    )

    ProductSearchContent(
        state = searchState(
            query = "iphone",
            content = SearchContentState.Success(BottomSheetState.Hidden)
        ),
        products = remember(products, append) {
            flowOf(
                PagingData.from(
                    data = products,
                    sourceLoadStates = LoadStates(
                        refresh = LoadState.NotLoading(endOfPaginationReached = false),
                        prepend = LoadState.NotLoading(endOfPaginationReached = true),
                        append = append,
                    ),
                ),
            )
        }.collectAsLazyPagingItems(),
        onEvent = {},
    )
}

fun searchState(query: String, content: SearchContentState) =
    ProductSearchScreenState.Search(
        query = query,
        isClearButtonVisible = query.isNotEmpty(),
        content = content,
    )
