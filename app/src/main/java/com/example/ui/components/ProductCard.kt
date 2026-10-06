package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.model.Product
import com.example.ui.theme.FarmeraGreenDark
import com.example.ui.theme.FarmeraStarYellow
import com.example.ui.theme.FarmeraSurfaceNeutral
import com.example.ui.theme.FarmeraTextMuted
import com.example.ui.theme.FarmeraTextPrimary
import com.example.ui.theme.FarmeraTextSecondary

@Composable
fun ProductCard(
    product: Product,
    quantityInCart: Int,
    onCardClick: () -> Unit,
    onAddToCart: () -> Unit,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    modifier: Modifier = Modifier,
    cardWidth: Dp = 160.dp,
    imageHeight: Dp = 140.dp
) {
    Column(
        modifier = modifier
            .width(cardWidth)
            .clickable { onCardClick() }
            .testTag("product_card_${product.id}")
    ) {
        // Image Container with soft neutral background and rounded corners
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(imageHeight)
                .clip(RoundedCornerShape(14.dp))
                .background(FarmeraSurfaceNeutral)
        ) {
            ProductAsyncImage(
                product = product,
                artworkPadding = 14.dp
            )

            // Optional top badge (e.g. "Direct Farm" or "Harvested this week")
            if (product.badgeText != null) {
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.White.copy(alpha = 0.92f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                        .align(Alignment.TopStart)
                ) {
                    Text(
                        text = product.badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FarmeraGreenDark
                    )
                }
            }

            // Stepper / Add button anchored at bottom-right inside container
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
            ) {
                CardAddButton(
                    quantity = quantityInCart,
                    onAdd = onAddToCart,
                    onIncrement = onIncrement,
                    onDecrement = onDecrement
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Product Name
        Text(
            text = product.name,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = FarmeraTextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(2.dp))

        // Rating and Provenance / Farm Location
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = FarmeraStarYellow,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "${product.rating}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = FarmeraTextPrimary
            )
            Text(
                text = " (${product.reviewsCount})",
                fontSize = 12.sp,
                color = FarmeraTextMuted
            )
            Text(
                text = " • ${product.sourceLocation.split(",").firstOrNull() ?: ""}",
                fontSize = 11.sp,
                color = FarmeraTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(start = 2.dp)
            )
        }

        Spacer(modifier = Modifier.height(3.dp))

        // Price and Purchase Unit
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = product.formattedPrice,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = FarmeraTextPrimary
            )
            Text(
                text = product.unit,
                fontSize = 11.sp,
                color = FarmeraTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
