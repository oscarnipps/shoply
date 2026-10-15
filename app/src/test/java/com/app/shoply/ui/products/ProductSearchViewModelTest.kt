package com.app.shoply.ui.products

import androidx.paging.LoadState
import com.app.shoply.FakeProductRepository
import com.app.shoply.domain.common.AppError
import com.app.shoply.domain.common.AppException
import com.app.shoply.util.MainDispatcherRule
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ProductSearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var repository: FakeProductRepository
    private lateinit var viewModel: ProductSearchViewModel

    @Before
    fun setUp() {
        repository = FakeProductRepository()
        viewModel = ProductSearchViewModel(repository, ProductUiMapper())
    }


    @Test
    fun `starts idle`() {
        assertEquals(ProductSearchScreenState.Idle, viewModel.screenState.value)
    }

    @Test
    fun `search mode active without query shows initial content`() {
        viewModel.onEvent(ProductSearchScreenEvent.SearchModeActive)

        assertEquals(search(query = "", content = SearchContentState.Initial), viewModel.screenState.value)
    }

    @Test
    fun `typing enters search mode and shows loading with clear button`() {
        viewModel.onEvent(ProductSearchScreenEvent.QueryChanged("iphone"))

        assertEquals(search(query = "iphone", content = SearchContentState.Loading), viewModel.screenState.value)
    }

    @Test
    fun `paging loading shows loading`() {
        typeQuery("iphone")

        viewModel.onEvent(ProductSearchScreenEvent.PagingLoadStateChanged(LoadState.Loading, itemCount = 0))

        assertEquals(SearchContentState.Loading, currentContent())
    }

    @Test
    fun `loaded items show success with hidden sheet`() {
        typeQuery("iphone")

        loaded(itemCount = 24)

        assertEquals(SearchContentState.Success(BottomSheetState.Hidden), currentContent())
    }

    @Test
    fun `loaded without items shows empty error`() {
        typeQuery("iphone")

        loaded(itemCount = 0)

        assertEquals(SearchContentState.Error(SearchError.Empty), currentContent())
    }

    @Test
    fun `NO_INTERNET maps to no internet error`() {
        typeQuery("iphone")

        failed(AppError.NO_INTERNET)

        assertEquals(SearchContentState.Error(SearchError.NoInternet), currentContent())
    }

    @Test
    fun `NO_DATA maps to empty error`() {
        typeQuery("iphone")

        failed(AppError.NO_DATA)

        assertEquals(SearchContentState.Error(SearchError.Empty), currentContent())
    }

    @Test
    fun `TECHNICAL maps to technical error`() {
        typeQuery("iphone")

        failed(AppError.TECHNICAL)

        assertEquals(SearchContentState.Error(SearchError.Technical), currentContent())
    }

    @Test
    fun `product click opens sheet and dismiss hides it`() {
        typeQuery("iphone")

        loaded(itemCount = 24)

        val product = uiModel()

        viewModel.onEvent(ProductSearchScreenEvent.ProductClicked(product))
        assertEquals(SearchContentState.Success(BottomSheetState.Visible(product)), currentContent())

        viewModel.onEvent(ProductSearchScreenEvent.BottomSheetDismissed)
        assertEquals(SearchContentState.Success(BottomSheetState.Hidden), currentContent())
    }

    @Test
    fun `clear keeps search mode and returns to initial`() {
        typeQuery("iphone")

        loaded(itemCount = 24)

        viewModel.onEvent(ProductSearchScreenEvent.ProductClicked(uiModel()))

        viewModel.onEvent(ProductSearchScreenEvent.ClearSearchClicked)

        assertEquals(search(query = "", content = SearchContentState.Initial), viewModel.screenState.value)
    }

    @Test
    fun `cancel leaves search mode`() {
        typeQuery("iphone")

        loaded(itemCount = 24)

        viewModel.onEvent(ProductSearchScreenEvent.CancelClicked)

        assertEquals(ProductSearchScreenState.Idle, viewModel.screenState.value)
    }

    @Test
    fun `search mode after cancel starts clean`() {
        typeQuery("iphone")

        loaded(itemCount = 24)

        viewModel.onEvent(ProductSearchScreenEvent.ProductClicked(uiModel()))

        viewModel.onEvent(ProductSearchScreenEvent.CancelClicked)

        viewModel.onEvent(ProductSearchScreenEvent.SearchModeActive)

        assertEquals(search(query = "", content = SearchContentState.Initial), viewModel.screenState.value)
    }

    @Test
    fun `no search happens on entry or when only entering search mode`() = runTest {
        collectProducts()

        viewModel.onEvent(ProductSearchScreenEvent.SearchModeActive)

        settle()

        assertTrue(repository.queries.isEmpty())
    }

    @Test
    fun `searches only once typing pauses for the debounce time`() = runTest {
        collectProducts()

        typeQuery("a")

        advanceTimeBy(100)

        typeQuery("ap")

        advanceTimeBy(100)

        typeQuery("apple")

        advanceTimeBy(DEBOUNCE_MS - 1)

        runCurrent()

        assertTrue(repository.queries.isEmpty())

        advanceTimeBy(2)

        runCurrent()

        assertEquals(listOf("apple"), repository.queries)
    }

    @Test
    fun `query is trimmed and identical queries are not searched again`() = runTest {
        collectProducts()

        typeQuery("  apple ")

        settle()

        typeQuery("apple")

        settle()

        assertEquals(listOf("apple"), repository.queries)
    }

    @Test
    fun `clearing does not search and typing the same query again reuses results`() = runTest {
        collectProducts()

        typeQuery("apple")

        settle()

        viewModel.onEvent(ProductSearchScreenEvent.ClearSearchClicked)

        settle()

        typeQuery("apple")

        settle()

        assertEquals(listOf("apple"), repository.queries)
    }

    @Test
    fun `new query triggers a new search`() = runTest {
        collectProducts()

        typeQuery("apple")

        settle()

        typeQuery("samsung")

        settle()

        assertEquals(listOf("apple", "samsung"), repository.queries)
    }

    @Test
    fun `retry searches the current query again`() = runTest {
        collectProducts()

        typeQuery("apple")

        settle()

        viewModel.onEvent(ProductSearchScreenEvent.RetryClicked)

        settle()

        assertEquals(listOf("apple", "apple"), repository.queries)
    }

    @Test
    fun `sheet and paging events do not trigger a search`() = runTest {
        collectProducts()

        typeQuery("apple")

        settle()

        loaded(itemCount = 24)

        viewModel.onEvent(ProductSearchScreenEvent.ProductClicked(uiModel()))

        viewModel.onEvent(ProductSearchScreenEvent.BottomSheetDismissed)

        settle()

        assertEquals(listOf("apple"), repository.queries)
    }

    private fun typeQuery(query: String) =
        viewModel.onEvent(ProductSearchScreenEvent.QueryChanged(query))

    private fun loaded(itemCount: Int) = viewModel.onEvent(
        ProductSearchScreenEvent.PagingLoadStateChanged(
            refresh = LoadState.NotLoading(endOfPaginationReached = false),
            itemCount = itemCount,
        ),
    )

    private fun failed(error: AppError) = viewModel.onEvent(
        ProductSearchScreenEvent.PagingLoadStateChanged(
            refresh = LoadState.Error(AppException(error)),
            itemCount = 0,
        ),
    )

    private fun currentContent(): SearchContentState =
        (viewModel.screenState.value as ProductSearchScreenState.Search).content

    private fun search(query: String, content: SearchContentState) = ProductSearchScreenState.Search(
        query = query,
        isClearButtonVisible = query.isNotEmpty(),
        content = content,
    )
    
    private fun TestScope.settle() {
        advanceTimeBy(DEBOUNCE_MS + 1)
        runCurrent()
    }

    private fun TestScope.collectProducts() {
        backgroundScope.launch { viewModel.products.collect {} }
        runCurrent()
    }

    private fun uiModel() = ProductUiModel(
        id = 1,
        name = "Apple iPhone 6 32GB Grijs",
        imageUrl = "https://image.coolblue.nl/300x750/products/818870",
        price = "€ 369,-",
        rating = "9,1",
        ratingFraction = 0.91f,
        reviewCount = 952,
        sellingPoints = persistentListOf("32 GB opslagcapaciteit"),
        availability = ProductAvailability.IN_STOCK,
        nextDayDelivery = true,
    )

    private companion object {
        const val DEBOUNCE_MS = 400L
    }
}