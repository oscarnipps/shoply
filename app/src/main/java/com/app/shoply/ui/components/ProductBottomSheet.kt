package com.app.shoply.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.shoply.R
import com.app.shoply.ui.products.ProductAvailability
import com.app.shoply.ui.products.ProductUiModel
import com.app.shoply.ui.theme.ShoplyColors
import com.app.shoply.ui.theme.ShoplyType
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductBottomSheet(
    product: ProductUiModel,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = ShoplyColors.Surface,
        contentColor = ShoplyColors.TextPrimary,
        scrimColor = ShoplyColors.Scrim,
        dragHandle = null,
    ) {
        ProductSheetContent(
            product = product,
            onCloseClick = {
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    if (!sheetState.isVisible) onDismiss()
                }
            },
        )
    }
}

@Composable
fun ProductSheetContent(
    product: ProductUiModel,
    onCloseClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .navigationBarsPadding()
            .padding(bottom = 24.dp),
    ) {
        SheetHeader(onCloseClick = onCloseClick)
        ProductSummary(product = product)
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp),
            color = ShoplyColors.Divider,
        )
        if (product.sellingPoints.isNotEmpty()) {
            Highlights(sellingPoints = product.sellingPoints)
        }
        if (product.reviewCount > 0) {
            ReviewsSummary(rating = product.rating, reviewCount = product.reviewCount)
        }
    }
}

@Composable
private fun SheetHeader(onCloseClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(modifier = Modifier.size(48.dp))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(ShoplyColors.SheetHandle),
            )
        }
        IconButton(onClick = onCloseClick) {
            Icon(
                painter = painterResource(R.drawable.ic_close),
                contentDescription = stringResource(R.string.close),
                modifier = Modifier.size(20.dp),
                tint = ShoplyColors.Icon,
            )
        }
    }
}

@Composable
private fun ProductSummary(product: ProductUiModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(start = 20.dp, end = 20.dp, bottom = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        ProductImage(
            imageUrl = product.imageUrl,
            cornerRadius = 14.dp,
            modifier = Modifier.size(width = 96.dp, height = 124.dp),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = product.name,
                style = ShoplyType.SheetTitle,
                color = ShoplyColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() },
            )
            ProductRating(
                rating = product.rating,
                ratingFraction = product.ratingFraction,
                reviewCount = product.reviewCount,
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = product.price,
                    style = ShoplyType.PriceSheet,
                    color = ShoplyColors.TextPrimary,
                    modifier = Modifier.alignByBaseline(),
                )
                Text(
                    text = stringResource(R.string.price_incl_vat),
                    style = ShoplyType.Label.copy(fontWeight = FontWeight.Normal),
                    color = ShoplyColors.TextSecondary,
                    modifier = Modifier.alignByBaseline(),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                AvailabilityLabel(availability = product.availability)
                if (product.nextDayDelivery) {
                    Text(text = "·", style = ShoplyType.BodySmall, color = ShoplyColors.IconMuted)
                    DeliveryLabel(text = stringResource(R.string.delivery_tomorrow_short))
                }
            }
        }
    }
}

@Composable
private fun Highlights(sellingPoints: List<String>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = stringResource(R.string.highlights),
            style = ShoplyType.SectionTitle,
            color = ShoplyColors.TextPrimary,
            modifier = Modifier.semantics { heading() },
        )
        sellingPoints.forEach { sellingPoint ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = ShoplyColors.Positive,
                )
                Text(
                    text = sellingPoint,
                    style = ShoplyType.BodyMedium,
                    color = ShoplyColors.TextPrimary
                )
            }
        }
    }
}

@Composable
private fun ReviewsSummary(rating: String, reviewCount: Int) {
    Row(
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(ShoplyColors.Background)
            .padding(start = 16.dp, end = 12.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(text = rating, style = ShoplyType.ReviewScore, color = ShoplyColors.TextPrimary)
        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = stringResource(R.string.reviews),
                style = ShoplyType.BodyMedium.copy(fontWeight = FontWeight.Bold),
                color = ShoplyColors.TextPrimary,
            )
            Text(
                text = stringResource(R.string.reviews_based_on, reviewCount),
                style = ShoplyType.BodySmall,
                color = ShoplyColors.Icon,
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = ShoplyColors.Icon,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductSheetPreview() = ProductSheetContent(
    product = ProductUiModel(
        id = 648317,
        name = "Apple iPhone 6s 32GB Space Gray",
        imageUrl = "https://image.coolblue.nl/300x750/products/1014060",
        price = "€ 509,-",
        rating = "9,2",
        ratingFraction = 0.92f,
        reviewCount = 1273,
        sellingPoints = persistentListOf(
            "32 GB opslagcapaciteit",
            "4,7 inch Retina HD scherm",
            "iOS 11"
        ),
        availability = ProductAvailability.IN_STOCK,
        nextDayDelivery = true,
    ),
    onCloseClick = {},
)