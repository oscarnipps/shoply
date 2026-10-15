package com.app.shoply.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemContentType
import androidx.paging.compose.itemKey
import com.app.shoply.R
import com.app.shoply.domain.common.AppError
import com.app.shoply.domain.common.AppException
import com.app.shoply.ui.products.ProductAvailability
import com.app.shoply.ui.products.ProductUiModel
import com.app.shoply.ui.theme.ShoplyColors
import com.app.shoply.ui.theme.ShoplyType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.flowOf

@Composable
fun ProductList(
    query: String,
    products: LazyPagingItems<ProductUiModel>,
    onProductClick: (ProductUiModel) -> Unit,
    onLoadMoreRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item(key = "results_header", contentType = "header") {
            ResultsHeader(query = query)
        }

        items(
            count = products.itemCount,
            key = products.itemKey { it.id },
            contentType = products.itemContentType { "product" },
        ) { index ->
            val product = products[index] ?: return@items
            ProductCard(product = product, onClick = { onProductClick(product) })
        }

        when (products.loadState.append) {
            is LoadState.Loading -> item(key = "append_loading", contentType = "footer") {
                LoadingMoreFooter()
            }

            is LoadState.Error -> item(key = "append_error", contentType = "footer") {
                LoadMoreErrorFooter(onRetry = onLoadMoreRetry)
            }

            is LoadState.NotLoading -> Unit
        }
    }
}

@Composable
private fun ResultsHeader(query: String) {
    val quotedQuery = "“$query”"
    val text = stringResource(R.string.results_for, quotedQuery)
    val queryStart = text.lastIndexOf(quotedQuery)

    Text(
        text = buildAnnotatedString {
            append(text.substring(0, queryStart))
            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = ShoplyColors.TextPrimary)) {
                append(quotedQuery)
            }
            append(text.substring(queryStart + quotedQuery.length))
        },
        style = ShoplyType.BodyMedium,
        color = ShoplyColors.TextSecondary,
        modifier = Modifier.padding(horizontal = 4.dp),
    )
}

@Composable
private fun LoadingMoreFooter() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(22.dp),
            color = ShoplyColors.Accent,
            trackColor = ShoplyColors.Border,
            strokeWidth = 3.dp,
        )
        Text(
            text = stringResource(R.string.loading_more),
            style = ShoplyType.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = ShoplyColors.TextSecondary,
        )
    }
}

@Composable
private fun LoadMoreErrorFooter(onRetry: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ShoplyColors.Surface)
            .border(1.dp, ShoplyColors.Border, shape)
            .padding(start = 16.dp, end = 8.dp, top = 10.dp, bottom = 10.dp)
            .semantics { liveRegion = LiveRegionMode.Polite },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_error_circle),
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = ShoplyColors.Negative,
        )
        Text(
            text = stringResource(R.string.load_more_error),
            style = ShoplyType.BodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = ShoplyColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )
        TextButton(
            onClick = onRetry,
            modifier = Modifier.height(44.dp),
            colors = ButtonDefaults.textButtonColors(contentColor = ShoplyColors.Accent),
        ) {
            Text(
                text = stringResource(R.string.load_more_retry),
                style = ShoplyType.BodyMedium.copy(fontWeight = FontWeight.ExtraBold),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun ResultsPreview() = ProductList(
    query = "iphone",
    onProductClick = {},
    onLoadMoreRetry = {},

    products = previewPagingItems(
        products = listOf(
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
            )
        ),
        append = LoadState.NotLoading(endOfPaginationReached = true),
    ),
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun ResultsPreviewWithLoadError() = ProductList(
    query = "iphone",
    onProductClick = {},
    onLoadMoreRetry = {},

    products = previewPagingItems(
        products = listOf(
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
            )
        ),
        append = LoadState.Error(
            AppException(
                appError = AppError.NO_DATA,
                exceptionMsg = "Something went wrong"
            )
        ),
    ),
)

@Composable
fun previewPagingItems(
    products: List<ProductUiModel> = emptyList(),
    append: LoadState = LoadState.NotLoading(endOfPaginationReached = true),
): LazyPagingItems<ProductUiModel> = remember(products, append) {
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
}.collectAsLazyPagingItems()
