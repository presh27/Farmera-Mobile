package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CartItem
import com.example.ui.theme.FarmeraBadgeRed
import com.example.ui.theme.FarmeraBorder
import com.example.ui.theme.FarmeraBorderLight
import com.example.ui.theme.FarmeraGreenPrimary
import com.example.ui.theme.FarmeraSurfaceNeutral
import com.example.ui.theme.FarmeraTextMuted
import com.example.ui.theme.FarmeraTextPrimary
import com.example.ui.theme.FarmeraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BasketBottomSheet(
    isOpen: Boolean,
    cartItems: List<CartItem>,
    onDismiss: () -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onGoToCart: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
) {
    if (!isOpen) return

    val subtotal = cartItems.sumOf { it.lineTotal }
    val totalCount = cartItems.sumOf { it.quantity }
    val freeDeliveryThreshold = 35000 // ₦35,000 for free delivery in Lagos
    val amountToFreeDelivery = (freeDeliveryThreshold - subtotal).coerceAtLeast(0)
    val progress = (subtotal.toFloat() / freeDeliveryThreshold.toFloat()).coerceIn(0f, 1f)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(42.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color(0xFFD1D5DB))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 6.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Basket Preview",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmeraTextPrimary
                )
                Text(
                    text = "$totalCount item${if (totalCount != 1) "s" else ""}",
                    fontSize = 13.sp,
                    color = FarmeraTextSecondary
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Cart Items List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = false)
            ) {
                items(cartItems, key = { it.serverItemId ?: "${it.product.id}_${it.product.slug}_${it.isBulk}" }) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Product image container
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(FarmeraSurfaceNeutral),
                            contentAlignment = Alignment.Center
                        ) {
                            ProductAsyncImage(
                                product = item.product,
                                artworkPadding = 4.dp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Product Name, Unit and Price
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp,
                                color = FarmeraTextPrimary,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.product.unit} • ${item.formattedLineTotal}",
                                fontSize = 13.sp,
                                color = FarmeraTextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Capsule Stepper
                        CardAddButton(
                            quantity = item.quantity,
                            onAdd = { onIncrement(item.product.id) },
                            onIncrement = { onIncrement(item.product.id) },
                            onDecrement = { onDecrement(item.product.id) }
                        )
                    }

                    HorizontalDivider(
                        color = FarmeraBorderLight,
                        thickness = 1.dp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Free delivery progress area matching screenshot
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                val progressText = if (amountToFreeDelivery > 0) {
                    "You are ₦${"%,d".format(amountToFreeDelivery)} away from free delivery"
                } else {
                    "You unlocked Free Delivery! 🎉"
                }

                Text(
                    text = progressText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = FarmeraTextPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = FarmeraGreenPrimary,
                    trackColor = Color(0xFFE5E7EB),
                    strokeCap = StrokeCap.Round
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Primary "Go to Cart (₦Total)" button matching reference
            Button(
                onClick = onGoToCart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("basket_sheet_go_to_cart"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = FarmeraGreenPrimary,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Go to Cart (₦${"%,d".format(subtotal)})",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Box(
                        modifier = Modifier.size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_basket),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .offset(x = 3.dp, y = (-3).dp)
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(FarmeraBadgeRed),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = totalCount.toString(),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
