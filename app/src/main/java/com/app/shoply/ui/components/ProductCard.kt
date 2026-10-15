package com.app.shoply.ui.components

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.app.shoply.R
import com.app.shoply.ui.products.ProductAvailability
import com.app.shoply.ui.products.ProductUiModel
import com.app.shoply.ui.theme.ShoplyColors
import com.app.shoply.ui.theme.ShoplyType
import kotlinx.collections.immutable.persistentListOf

private const val MAX_CARD_SELLING_POINTS = 2
private const val OUT_OF_STOCK_IMAGE_ALPHA = 0.55f

@Composable
fun ProductCard(
    product: ProductUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isOutOfStock = product.availability == ProductAvailability.OUT_OF_STOCK
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .background(ShoplyColors.Surface)
            .border(1.dp, ShoplyColors.Border, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProductImage(
            imageUrl = product.imageUrl,
            cornerRadius = 10.dp,
            modifier = Modifier
                .size(width = 96.dp, height = 128.dp)
                .alpha(if (isOutOfStock) OUT_OF_STOCK_IMAGE_ALPHA else 1f),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = product.name,
                style = ShoplyType.CardTitle,
                color = ShoplyColors.TextPrimary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            ProductRating(
                rating = product.rating,
                ratingFraction = product.ratingFraction,
                reviewCount = product.reviewCount,
            )
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                product.sellingPoints.take(MAX_CARD_SELLING_POINTS).forEach { sellingPoint ->
                    Text(
                        text = "· $sellingPoint",
                        style = ShoplyType.BodySmall,
                        color = ShoplyColors.TextSecondary,
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = product.price,
                    style = ShoplyType.PriceCard,
                    color = if (isOutOfStock) ShoplyColors.TextSecondary else ShoplyColors.TextPrimary,
                )
                AvailabilityChip(availability = product.availability)
            }
            if (product.nextDayDelivery) {
                DeliveryLabel(text = stringResource(R.string.delivery_tomorrow))
            }
        }
    }
}

@Composable
fun ProductImage(
    imageUrl: String,
    cornerRadius: Dp,
    modifier: Modifier = Modifier,
) {
    var hasError by remember(imageUrl) { mutableStateOf(false) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(ShoplyColors.SurfaceMuted),
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            onSuccess = { hasError = false },
            onError = { hasError = true },
        )
        if (hasError) {
            Icon(
                painter = painterResource(R.drawable.ic_image_placeholder),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = ShoplyColors.IconMuted,
            )
        }
    }
}

@Composable
fun ProductRating(
    rating: String,
    ratingFraction: Float,
    reviewCount: Int,
    modifier: Modifier = Modifier,
) {
    if (reviewCount == 0) {
        Row(
            modifier = modifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                modifier = Modifier.size(14.dp),
                tint = ShoplyColors.StarEmpty,
            )
            Text(
                text = stringResource(R.string.no_reviews),
                style = ShoplyType.BodySmall.copy(fontWeight = FontWeight.Bold),
                color = ShoplyColors.TextSecondary,
            )
        }
    } else {
        val description = stringResource(R.string.rating_a11y, rating, reviewCount)
        Row(
            modifier = modifier.clearAndSetSemantics { contentDescription = description },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            StarRating(fraction = ratingFraction)
            Text(
                text = rating,
                style = ShoplyType.BodySmall.copy(fontWeight = FontWeight.Bold),
                color = ShoplyColors.TextPrimary,
            )
            Text(
                text = stringResource(R.string.review_count, reviewCount),
                style = ShoplyType.BodySmall,
                color = ShoplyColors.TextSecondary,
            )
        }
    }
}

@Composable
fun StarRating(
    fraction: Float,
    modifier: Modifier = Modifier,
    starSize: Dp = 14.dp,
) {
    Box(modifier = modifier) {
        StarRow(color = ShoplyColors.StarEmpty, starSize = starSize)
        StarRow(
            color = ShoplyColors.StarFilled,
            starSize = starSize,
            modifier = Modifier.drawWithContent {
                clipRect(right = size.width * fraction) { this@drawWithContent.drawContent() }
            },
        )
    }
}

@Composable
private fun StarRow(color: Color, starSize: Dp, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(1.dp)) {
        repeat(5) {
            Icon(
                painter = painterResource(R.drawable.ic_star),
                contentDescription = null,
                modifier = Modifier.size(starSize),
                tint = color,
            )
        }
    }
}


@Composable
fun AvailabilityChip(
    availability: ProductAvailability,
    modifier: Modifier = Modifier,
) {
    val style = availability.toStyle()
    Row(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(style.container)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Dot(color = style.dot, size = 6.dp)
        Text(
            text = stringResource(style.label),
            style = ShoplyType.Label,
            color = style.content,
            maxLines = 1
        )
    }
}

@Composable
fun AvailabilityLabel(
    availability: ProductAvailability,
    modifier: Modifier = Modifier,
) {
    val style = availability.toStyle()
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Dot(color = style.dot, size = 8.dp)
        Text(
            text = stringResource(style.label),
            style = ShoplyType.BodySmall.copy(fontWeight = FontWeight.Bold),
            color = style.content,
        )
    }
}

@Composable
fun DeliveryLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_delivery),
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = ShoplyColors.Positive,
        )
        Text(
            text = text,
            style = ShoplyType.Label.copy(fontWeight = FontWeight.SemiBold),
            color = ShoplyColors.Positive,
        )
    }
}

@Composable
private fun Dot(color: Color, size: Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
    )
}

internal data class AvailabilityStyle(
    @StringRes val label: Int,
    val content: Color,
    val container: Color,
    val dot: Color,
)

internal fun ProductAvailability.toStyle(): AvailabilityStyle = when (this) {
    ProductAvailability.IN_STOCK -> AvailabilityStyle(
        label = R.string.availability_in_stock,
        content = ShoplyColors.Positive,
        container = ShoplyColors.PositiveContainer,
        dot = ShoplyColors.PositiveDot,
    )

    ProductAvailability.LOW_STOCK -> AvailabilityStyle(
        label = R.string.availability_low_stock,
        content = ShoplyColors.Warning,
        container = ShoplyColors.WarningContainer,
        dot = ShoplyColors.WarningDot,
    )

    ProductAvailability.OUT_OF_STOCK -> AvailabilityStyle(
        label = R.string.availability_out_of_stock,
        content = ShoplyColors.Negative,
        container = ShoplyColors.NegativeContainer,
        dot = ShoplyColors.NegativeDot,
    )

    ProductAvailability.UNKNOWN -> AvailabilityStyle(
        label = R.string.availability_unknown,
        content = ShoplyColors.TextSecondary,
        container = ShoplyColors.IconCircle,
        dot = ShoplyColors.IconMuted,
    )
}

@Preview(widthDp = 390, showBackground = true, backgroundColor = 0xFFF4F3EF)
@Composable
private fun ProductCardStatesPreview() {
    Column(
        modifier = Modifier
            .background(ShoplyColors.Background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        ProductCard(
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
            onClick = {}
        )
    }
}