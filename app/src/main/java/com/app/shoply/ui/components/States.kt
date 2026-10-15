package com.app.shoply.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.app.shoply.R
import com.app.shoply.ui.products.BottomSheetState
import com.app.shoply.ui.products.ProductSearchContent
import com.app.shoply.ui.products.ProductSearchScreenState
import com.app.shoply.ui.products.SearchContentState
import com.app.shoply.ui.products.SearchError
import com.app.shoply.ui.products.searchState
import com.app.shoply.ui.theme.ShoplyColors
import com.app.shoply.ui.theme.ShoplyType

@Composable
fun InitialContent(modifier: Modifier = Modifier) {
    StatusMessage(
        icon = R.drawable.ic_shopping_bag,
        title = stringResource(R.string.launch_title),
        message = stringResource(R.string.launch_message),
        modifier = modifier,
    )
}

@Composable
fun EmptyContent(
    query: String,
    onClearSearchClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatusMessage(
        icon = R.drawable.ic_search_off,
        title = stringResource(R.string.empty_title, query),
        message = stringResource(R.string.empty_message),
        modifier = modifier,
    ) {
        OutlinedButton(
            onClick = onClearSearchClick,
            modifier = Modifier.height(48.dp),
            shape = CircleShape,
            border = BorderStroke(1.dp, ShoplyColors.OutlineButtonBorder),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ShoplyColors.Surface,
                contentColor = ShoplyColors.TextPrimary,
            ),
            contentPadding = PaddingValues(horizontal = 24.dp),
        ) {
            Text(text = stringResource(R.string.empty_action), style = ShoplyType.Button)
        }
    }
}

@Composable
fun NoInternetContent(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatusMessage(
        icon = R.drawable.ic_wifi_off,
        title = stringResource(R.string.offline_title),
        message = stringResource(R.string.offline_message),
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        RetryButton(onClick = onRetryClick)
    }
}

@Composable
fun TechnicalErrorContent(
    onRetryClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    StatusMessage(
        icon = R.drawable.ic_warning,
        title = stringResource(R.string.error_title),
        message = stringResource(R.string.error_message),
        iconTint = ShoplyColors.Negative,
        iconContainer = ShoplyColors.NegativeContainer,
        modifier = modifier.semantics { liveRegion = LiveRegionMode.Polite },
    ) {
        RetryButton(onClick = onRetryClick)
    }
}

@Composable
private fun StatusMessage(
    @DrawableRes icon: Int,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    iconTint: Color = ShoplyColors.Icon,
    iconContainer: Color = ShoplyColors.IconCircle,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 40.dp, end = 40.dp, bottom = 120.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(14.dp, Alignment.CenterVertically),
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(iconContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier.size(44.dp),
                tint = iconTint,
            )
        }
        Text(
            text = title,
            style = ShoplyType.Title,
            color = ShoplyColors.TextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.semantics { heading() },
        )
        Text(
            text = message,
            style = ShoplyType.Body,
            color = ShoplyColors.TextSecondary,
            textAlign = TextAlign.Center,
        )
        if (action != null) {
            Spacer(modifier = Modifier.height(8.dp))
            action()
        }
    }
}

@Composable
private fun RetryButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.height(48.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = ShoplyColors.Accent,
            contentColor = ShoplyColors.Surface,
        ),
        contentPadding = PaddingValues(horizontal = 28.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_retry),
            contentDescription = null,
            modifier = Modifier.size(18.dp),
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = stringResource(R.string.retry), style = ShoplyType.Button)
    }
}


@Composable
fun LoadingShimmerContent(
    modifier: Modifier = Modifier,
    itemCount: Int = 4,
) {
    val transition = rememberInfiniteTransition(label = "skeleton")

    val alpha by transition.animateFloat(
        initialValue = 1f,
        targetValue = 0.55f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "skeleton_alpha",
    )

    val loadingDescription = stringResource(R.string.loading_results)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .clearAndSetSemantics { contentDescription = loadingDescription },
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Bone(
            alpha = { alpha },
            radius = 8.dp,
            modifier = Modifier
                .padding(horizontal = 4.dp, vertical = 2.dp)
                .size(width = 140.dp, height = 16.dp),
        )
        repeat(itemCount) { SkeletonCard(alpha = { alpha }) }
    }
}

@Composable
private fun SkeletonCard(alpha: () -> Float) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(ShoplyColors.Surface)
            .border(1.dp, ShoplyColors.Border, shape)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Bone(alpha, radius = 10.dp, modifier = Modifier.size(width = 96.dp, height = 128.dp))
        Column(
            modifier = Modifier
                .weight(1f)
                .height(128.dp)
                .padding(top = 2.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Bone(
                alpha, radius = 7.dp, modifier = Modifier
                    .fillMaxWidth()
                    .height(14.dp)
            )
            Bone(
                alpha, radius = 7.dp, modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(14.dp)
            )
            Bone(
                alpha,
                radius = 6.dp,
                modifier = Modifier
                    .padding(top = 4.dp)
                    .fillMaxWidth(0.55f)
                    .height(12.dp)
            )
            Bone(
                alpha, radius = 6.dp, modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(12.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Bone(alpha, radius = 8.dp, modifier = Modifier.size(width = 72.dp, height = 20.dp))
                Bone(alpha, radius = 12.dp, modifier = Modifier.size(width = 76.dp, height = 24.dp))
            }
        }
    }
}

@Composable
private fun Bone(
    alpha: () -> Float,
    radius: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .graphicsLayer { this.alpha = alpha() }
            .clip(RoundedCornerShape(radius))
            .background(ShoplyColors.Skeleton),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun IdlePreview() = ProductSearchContent(
    state = ProductSearchScreenState.Idle,
    products = previewPagingItems(emptyList()),
    onEvent = {},
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun InitialPreview() = ProductSearchContent(
    state = searchState(query = "", content = SearchContentState.Initial),
    products = previewPagingItems(emptyList()),
    onEvent = {},
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun LoadingPreview() = ProductSearchContent(
    state = searchState(query = "iphone", content = SearchContentState.Loading),
    products = previewPagingItems(emptyList()),
    onEvent = {},
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun ResultsPreview() = ProductSearchContent(
    state = searchState(
        query = "iphone",
        content = SearchContentState.Success(BottomSheetState.Hidden)
    ),
    products = previewPagingItems(),
    onEvent = {},
)


@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun EmptyPreview() = ProductSearchContent(
    state = searchState(query = "iphnoe", content = SearchContentState.Error(SearchError.Empty)),
    products = previewPagingItems(emptyList()),
    onEvent = {},
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun NoInternetPreview() = ProductSearchContent(
    state = searchState(
        query = "iphone",
        content = SearchContentState.Error(SearchError.NoInternet)
    ),
    products = previewPagingItems(emptyList()),
    onEvent = {},
)

@Preview(showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun TechnicalErrorPreview() = ProductSearchContent(
    state = searchState(
        query = "iphone",
        content = SearchContentState.Error(SearchError.Technical)
    ),
    products = previewPagingItems(emptyList()),
    onEvent = {},
)
