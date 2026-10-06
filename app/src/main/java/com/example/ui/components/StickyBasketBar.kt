package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CartItem
import com.example.ui.theme.FarmeraBadgeRed
import com.example.ui.theme.FarmeraGreenPrimary

@Composable
fun StickyBasketBar(
    cartItems: List<CartItem>,
    onViewBasket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val totalItems = cartItems.sumOf { it.quantity }
    val isVisible = totalItems > 0

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(6.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(FarmeraGreenPrimary)
                    .clickable { onViewBasket() }
                    .padding(horizontal = 14.dp)
                    .testTag("sticky_basket_bar"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Circular product thumbnails of added items (matching reference screenshots)
                Row(
                    horizontalArrangement = Arrangement.spacedBy((-6).dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val distinctItems = cartItems.take(5)
                    distinctItems.forEach { item ->
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            ProductAsyncImage(
                                product = item.product,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(CircleShape),
                                artworkPadding = 3.dp,
                                backgroundColor = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Vertical subtle divider line
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(26.dp)
                        .background(Color.White.copy(alpha = 0.4f))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // "View Basket" text
                Text(
                    text = "View Basket",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

                // Basket icon with red circular badge
                Box(
                    modifier = Modifier.size(36.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_basket),
                        contentDescription = "Basket",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )

                    // Red count badge
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 4.dp, y = (-3).dp)
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(FarmeraBadgeRed),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (totalItems > 99) "99+" else totalItems.toString(),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
