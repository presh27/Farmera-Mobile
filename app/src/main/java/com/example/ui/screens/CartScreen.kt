package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.RadioButtonChecked
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.CartItem
import com.example.ui.components.CardAddButton
import com.example.ui.components.ProduceArtwork
import com.example.ui.components.ProductAsyncImage
import com.example.ui.theme.FarmeraBorder
import com.example.ui.theme.FarmeraBorderLight
import com.example.ui.theme.FarmeraGreenDark
import com.example.ui.theme.FarmeraGreenPrimary
import com.example.ui.theme.FarmeraMintCard
import com.example.ui.theme.FarmeraMintSurface
import com.example.ui.theme.FarmeraSurfaceNeutral
import com.example.ui.theme.FarmeraTextMuted
import com.example.ui.theme.FarmeraTextPrimary
import com.example.ui.theme.FarmeraTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    cartItems: List<CartItem>,
    syncStatusText: String,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    onRemove: (String) -> Unit,
    onBrowseMarket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val subtotal = cartItems.sumOf { it.lineTotal }
    val isFreeDelivery = subtotal >= 35000
    val deliveryFee = if (isFreeDelivery || subtotal == 0) 0 else 2500
    val bagFee = if (subtotal > 0) 250 else 0
    val serviceFee = if (subtotal > 0) 750 else 0
    val total = subtotal + deliveryFee + bagFee + serviceFee

    var isCheckoutSheetOpen by remember { mutableStateOf(false) }
    var orderPlacedSuccess by remember { mutableStateOf(false) }
    var selectedDeliveryMethod by remember { mutableStateOf("standard") }
    var requestInvoice by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .testTag("cart_screen")
    ) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Your cart",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmeraTextPrimary
                )
                // Supporting status "Synced across Farmera"
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = null,
                        tint = FarmeraGreenDark,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = syncStatusText,
                        fontSize = 11.sp,
                        color = FarmeraTextSecondary
                    )
                }
            }

            if (cartItems.isNotEmpty()) {
                val totalCount = cartItems.sumOf { it.quantity }
                Text(
                    text = "$totalCount items",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = FarmeraTextSecondary
                )
            }
        }

        if (cartItems.isEmpty()) {
            // Empty Basket State
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape)
                            .background(FarmeraMintSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_basket_dark),
                            contentDescription = null,
                            tint = FarmeraGreenPrimary,
                            modifier = Modifier.size(42.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Your basket is empty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmeraTextPrimary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Explore fresh tubers, grains, vegetables and pantry staples straight from verified farms across Nigeria.",
                        fontSize = 14.sp,
                        color = FarmeraTextSecondary,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = onBrowseMarket,
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("empty_cart_browse_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FarmeraGreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Browse the market",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        } else {
            // Cart Items List & Summary Breakdown
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 100.dp)
            ) {
                // Free Delivery Threshold Notice
                item {
                    val remaining = (35000 - subtotal).coerceAtLeast(0)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(FarmeraMintCard)
                            .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Text(
                            text = if (remaining > 0)
                                "Add ₦${"%,d".format(remaining)} more of farm produce for Free Delivery!"
                            else
                                "🎉 Free Delivery Unlocked for your order!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = FarmeraGreenDark
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // Item rows
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
                                .size(64.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(FarmeraSurfaceNeutral),
                            contentAlignment = Alignment.Center
                        ) {
                            ProductAsyncImage(
                                product = item.product,
                                artworkPadding = 6.dp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Product Details
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.product.name,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                color = FarmeraTextPrimary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${item.product.sourceLocation.split(",").firstOrNull() ?: ""} • ${item.product.unit}",
                                fontSize = 12.sp,
                                color = FarmeraTextSecondary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.formattedLineTotal,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmeraTextPrimary
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Stepper control
                        CardAddButton(
                            quantity = item.quantity,
                            onAdd = { onIncrement(item.product.id) },
                            onIncrement = { onIncrement(item.product.id) },
                            onDecrement = { onDecrement(item.product.id) }
                        )

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { onRemove(item.product.id) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete item",
                                tint = FarmeraTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    HorizontalDivider(color = FarmeraBorderLight, thickness = 1.dp)
                }

                // Summary Section
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Order Summary",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmeraTextPrimary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFFF9FAFB))
                            .border(1.dp, FarmeraBorder, RoundedCornerShape(14.dp))
                            .padding(16.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SummaryLine(label = "Subtotal", value = "₦${"%,d".format(subtotal)}")
                            SummaryLine(
                                label = "Delivery Fee",
                                value = if (deliveryFee == 0) "Free" else "₦${"%,d".format(deliveryFee)}"
                            )
                            SummaryLine(label = "Estimated Packing & Bag", value = "₦${"%,d".format(bagFee)}")
                            SummaryLine(label = "Service fee", value = "₦${"%,d".format(serviceFee)}")

                            HorizontalDivider(
                                color = FarmeraBorder,
                                thickness = 1.dp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Estimated Total",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmeraTextPrimary
                                )
                                Text(
                                    text = "₦${"%,d".format(total)}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmeraGreenDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary CTA "Continue to checkout"
                    Button(
                        onClick = { isCheckoutSheetOpen = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("cart_continue_to_checkout_cta"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FarmeraGreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Continue to checkout • ₦${"%,d".format(total)}",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }

    // Checkout Bottom Sheet matching Checkout.png reference screenshot
    if (isCheckoutSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { isCheckoutSheetOpen = false },
            sheetState = sheetState,
            containerColor = Color.White,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
            ) {
                // Title matching Checkout.png
                Text(
                    text = "Checkout",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmeraTextPrimary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(16.dp))

                if (orderPlacedSuccess) {
                    // Order confirmed feedback
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = FarmeraGreenPrimary,
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Order Placed Successfully!",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmeraTextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Order #FAR-${(1000..9999).random()} has been sent to our farm dispatch team in Lagos.",
                            fontSize = 13.sp,
                            color = FarmeraTextSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = {
                                orderPlacedSuccess = false
                                isCheckoutSheetOpen = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = FarmeraGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Done")
                        }
                    }
                } else {
                    // Delivery Speed selection matching Checkout.png
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, FarmeraBorder, RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDeliveryMethod = "priority" }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (selectedDeliveryMethod == "priority") Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (selectedDeliveryMethod == "priority") FarmeraGreenPrimary else FarmeraTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Priority Farm Direct (Today, 2 - 4 hrs)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = FarmeraTextPrimary
                                )
                                Text(
                                    text = "Direct farm cold transit to Lagos door",
                                    fontSize = 11.sp,
                                    color = FarmeraTextSecondary
                                )
                            }
                        }

                        HorizontalDivider(color = FarmeraBorderLight)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedDeliveryMethod = "standard" }
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (selectedDeliveryMethod == "standard") Icons.Default.RadioButtonChecked else Icons.Default.RadioButtonUnchecked,
                                contentDescription = null,
                                tint = if (selectedDeliveryMethod == "standard") FarmeraGreenPrimary else FarmeraTextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Standard Scheduled (Next Morning)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = FarmeraTextPrimary
                                )
                                Text(
                                    text = "Harvested at dawn, delivered by 11am",
                                    fontSize = 11.sp,
                                    color = FarmeraTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Breakdown lines matching Checkout.png
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF9FAFB))
                            .border(1.dp, FarmeraBorder, RoundedCornerShape(12.dp))
                            .padding(14.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            SummaryLine(label = "Subtotal", value = "₦${"%,d".format(subtotal)}")
                            SummaryLine(label = "Bag fee", value = "₦${"%,d".format(bagFee)}")
                            SummaryLine(label = "Service fee", value = "₦${"%,d".format(serviceFee)}")
                            SummaryLine(
                                label = "Delivery",
                                value = if (deliveryFee == 0) "Free" else "₦${"%,d".format(deliveryFee)}"
                            )
                            HorizontalDivider(color = FarmeraBorder, modifier = Modifier.padding(vertical = 4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Total",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmeraTextPrimary
                                )
                                Text(
                                    text = "₦${"%,d".format(total)}",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = FarmeraTextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Request invoice switch matching Checkout.png
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Request an invoice",
                                    fontSize = 13.sp,
                                    color = FarmeraTextPrimary
                                )
                                Switch(
                                    checked = requestInvoice,
                                    onCheckedChange = { requestInvoice = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = FarmeraGreenPrimary)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Payment method row matching Checkout.png
                    Text(
                        text = "Payment method",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FarmeraTextPrimary
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .border(1.dp, FarmeraBorder, RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "💳 Paystack (Debit Card / Bank Transfer)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = FarmeraTextPrimary
                            )
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = FarmeraTextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Place Order button matching Checkout.png
                    Button(
                        onClick = { orderPlacedSuccess = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("checkout_place_order_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = FarmeraGreenPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Place Order",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun SummaryLine(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = FarmeraTextSecondary
        )
        Text(
            text = value,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = FarmeraTextPrimary
        )
    }
}
