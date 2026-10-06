package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.example.R
import com.example.model.Product
import com.example.ui.components.ProduceArtwork
import com.example.ui.components.ProductAsyncImage
import com.example.ui.theme.FarmeraBadgeRed
import com.example.ui.theme.FarmeraBorder
import com.example.ui.theme.FarmeraBorderLight
import com.example.ui.theme.FarmeraGreenDark
import com.example.ui.theme.FarmeraGreenPrimary
import com.example.ui.theme.FarmeraMintCard
import com.example.ui.theme.FarmeraMintSurface
import com.example.ui.theme.FarmeraStarYellow
import com.example.ui.theme.FarmeraSurfaceNeutral
import com.example.ui.theme.FarmeraTextMuted
import com.example.ui.theme.FarmeraTextPrimary
import com.example.ui.theme.FarmeraTextSecondary

@Composable
fun ProductDetailScreen(
    product: Product,
    quantityInCart: Int,
    cartItemCount: Int = 0,
    onBackClick: () -> Unit,
    onAddToCart: (Product, Boolean, Int) -> Unit,
    onOpenBasket: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var isBulkSelected by remember { mutableStateOf(false) }
    var localQuantity by remember { mutableStateOf(if (quantityInCart > 0) quantityInCart else 1) }

    val activePrice = if (isBulkSelected && product.bulkPrice != null) product.bulkPrice else product.price
    val formattedActivePrice = "₦${"%,d".format(activePrice)}"
    val formattedTotalPrice = "₦${"%,d".format(activePrice * localQuantity)}"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .testTag("product_detail_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(bottom = 100.dp) // Room for sticky bottom CTA
        ) {
            // Top Navigation Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("detail_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = FarmeraTextPrimary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = FarmeraTextPrimary
                        )
                    }

                    // Shared reactive cart badge indicator
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clickable { onOpenBasket() }
                            .testTag("detail_cart_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_basket_dark),
                            contentDescription = "Cart",
                            tint = FarmeraTextPrimary,
                            modifier = Modifier.size(24.dp)
                        )

                        if (cartItemCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = 2.dp, y = 2.dp)
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(FarmeraBadgeRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (cartItemCount > 99) "99+" else cartItemCount.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }

            // Large Swipeable Image Gallery
            val galleryPagerState = rememberPagerState(pageCount = { 3 })
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                HorizontalPager(
                    state = galleryPagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(FarmeraSurfaceNeutral)
                ) { page ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        ProductAsyncImage(
                            product = product,
                            artworkPadding = 28.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Gallery Pager Dots
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(3) { index ->
                        val isSelected = galleryPagerState.currentPage == index
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 3.dp)
                                .height(4.dp)
                                .width(if (isSelected) 14.dp else 5.dp)
                                .clip(CircleShape)
                                .background(if (isSelected) FarmeraGreenPrimary else Color(0xFFD1D5DB))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Main Details
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                // Category & Availability Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.category.uppercase(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmeraGreenDark,
                        letterSpacing = 0.5.sp
                    )

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(FarmeraMintSurface)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = product.availability,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = FarmeraGreenDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Product Title
                Text(
                    text = product.name,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmeraTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Provenance & Rating
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = FarmeraStarYellow,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${product.rating} (${product.reviewsCount} reviews)",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = FarmeraTextPrimary
                    )
                    Text(
                        text = " • ${product.sourceLocation}",
                        fontSize = 13.sp,
                        color = FarmeraTextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Price and Unit
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = formattedActivePrice,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmeraTextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "per ${product.unit}",
                        fontSize = 14.sp,
                        color = FarmeraTextSecondary
                    )
                }

                // Retail vs Bulk Option Selector (if bulk available)
                if (product.isBulkAvailable && product.bulkPrice != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Purchase Type",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = FarmeraTextPrimary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Household Retail Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.5.dp,
                                    if (!isBulkSelected) FarmeraGreenPrimary else FarmeraBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .background(if (!isBulkSelected) FarmeraMintCard else Color.White)
                                .clickable { isBulkSelected = false }
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = "Household",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (!isBulkSelected) FarmeraGreenDark else FarmeraTextPrimary
                                )
                                Text(
                                    text = product.formattedPrice,
                                    fontSize = 12.sp,
                                    color = FarmeraTextSecondary
                                )
                            }
                        }

                        // Bulk Buy Option
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(
                                    1.5.dp,
                                    if (isBulkSelected) FarmeraGreenPrimary else FarmeraBorder,
                                    RoundedCornerShape(10.dp)
                                )
                                .background(if (isBulkSelected) FarmeraMintCard else Color.White)
                                .clickable { isBulkSelected = true }
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Bulk / Business",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isBulkSelected) FarmeraGreenDark else FarmeraTextPrimary
                                    )
                                }
                                Text(
                                    text = "${product.formattedBulkPrice} (Save 12%)",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = FarmeraGreenDark
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // FROM THE SOURCE Block (as specified in prompt)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(FarmeraSurfaceNeutral)
                        .border(1.dp, FarmeraBorder, RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = FarmeraGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "FROM THE SOURCE",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmeraGreenDark,
                                letterSpacing = 0.5.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = product.farmName,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmeraTextPrimary
                        )

                        Text(
                            text = product.sourceLocation,
                            fontSize = 13.sp,
                            color = FarmeraTextSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = FarmeraGreenPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Verified Farmera partner",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = FarmeraGreenDark
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Information Sections
                InfoSection(
                    icon = Icons.Default.Info,
                    title = "About this produce",
                    content = product.shortDescription
                )

                HorizontalDivider(color = FarmeraBorderLight, modifier = Modifier.padding(vertical = 14.dp))

                InfoSection(
                    icon = Icons.Default.Yard,
                    title = "Harvest & provenance",
                    content = product.harvestInfo
                )

                HorizontalDivider(color = FarmeraBorderLight, modifier = Modifier.padding(vertical = 14.dp))

                InfoSection(
                    icon = Icons.Default.LocalShipping,
                    title = "Delivery information",
                    content = product.deliveryInfo
                )

                HorizontalDivider(color = FarmeraBorderLight, modifier = Modifier.padding(vertical = 14.dp))

                InfoSection(
                    icon = Icons.Default.Thermostat,
                    title = "Storage guidance",
                    content = product.storageGuidance
                )
            }
        }

        // Sticky Add to Cart Bottom Bar
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color.White)
                .border(1.dp, FarmeraBorderLight)
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Quantity Stepper
                Row(
                    modifier = Modifier
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(FarmeraSurfaceNeutral)
                        .border(1.dp, FarmeraBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { if (localQuantity > 1) localQuantity-- },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "−",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmeraTextPrimary
                        )
                    }

                    Text(
                        text = localQuantity.toString(),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmeraTextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .clickable { localQuantity++ },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "+",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmeraGreenDark
                        )
                    }
                }

                // Add to Cart Button
                Button(
                    onClick = {
                        onAddToCart(product, isBulkSelected, localQuantity)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("detail_add_to_cart_cta"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = FarmeraGreenPrimary,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = "Add to Cart • $formattedTotalPrice",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailScreen(
    product: Product,
    quantityInCart: Int,
    cartItemCount: Int = 0,
    onBackClick: () -> Unit,
    onAddToCart: (Product, Boolean) -> Unit,
    onOpenBasket: () -> Unit,
    modifier: Modifier = Modifier
) {
    ProductDetailScreen(
        product = product,
        quantityInCart = quantityInCart,
        cartItemCount = cartItemCount,
        onBackClick = onBackClick,
        onAddToCart = { prod, isBulk, count ->
            repeat(count) { onAddToCart(prod, isBulk) }
        },
        onOpenBasket = onOpenBasket,
        modifier = modifier
    )
}

@Composable
private fun InfoSection(
    icon: ImageVector,
    title: String,
    content: String
) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FarmeraGreenDark,
                modifier = Modifier.size(17.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = FarmeraTextPrimary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 13.sp,
            color = FarmeraTextSecondary,
            lineHeight = 19.sp
        )
    }
}
