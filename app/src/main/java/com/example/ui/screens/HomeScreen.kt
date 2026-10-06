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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BannerSlide
import com.example.model.Category
import com.example.model.Product
import com.example.ui.components.CategoryRow
import com.example.ui.components.FarmeraTopBar
import com.example.ui.components.HeroCarousel
import com.example.ui.components.ProductCard
import com.example.ui.theme.FarmeraBorder
import com.example.ui.theme.FarmeraGreenDark
import com.example.ui.theme.FarmeraGreenPrimary
import com.example.ui.theme.FarmeraMintCard
import com.example.ui.theme.FarmeraTextMuted
import com.example.ui.theme.FarmeraTextPrimary
import com.example.ui.theme.FarmeraTextSecondary

@Composable
fun HomeScreen(
    selectedLocation: String,
    cartItemCount: Int,
    heroSlides: List<BannerSlide>,
    categories: List<Category>,
    selectedCategorySlug: String?,
    freshFromFarms: List<Product>,
    harvestingThisWeek: List<Product>,
    bulkBuys: List<Product>,
    getProductQuantity: (String) -> Int,
    onLocationClick: () -> Unit,
    onBasketClick: () -> Unit,
    onSearchClick: () -> Unit,
    onHeroCtaClick: (BannerSlide) -> Unit,
    onSelectCategory: (Category) -> Unit,
    onSeeAllClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .testTag("home_screen")
    ) {
        // Top Delivery Context and Basket Button
        FarmeraTopBar(
            currentLocation = selectedLocation,
            cartItemCount = cartItemCount,
            onLocationClick = onLocationClick,
            onBasketClick = onBasketClick
        )

        // Search Input Bar Shortcut
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFF9FAFB))
                .border(1.dp, FarmeraBorder, RoundedCornerShape(12.dp))
                .clickable { onSearchClick() }
                .padding(horizontal = 14.dp, vertical = 12.dp)
                .testTag("home_search_bar_trigger")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = FarmeraTextMuted,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Search yam, garri, palm oil...",
                    fontSize = 14.sp,
                    color = FarmeraTextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Main Home Scrollable Feed
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp) // Room for sticky basket bar + bottom nav
        ) {
            // 1. Promotional Carousel
            item {
                HeroCarousel(
                    slides = heroSlides,
                    onCtaClick = onHeroCtaClick,
                    modifier = Modifier.padding(bottom = 18.dp)
                )
            }

            // 2. Horizontally scrollable Category Icons
            item {
                Column(modifier = Modifier.padding(bottom = 20.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Categories",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = FarmeraTextPrimary
                        )
                        TextButton(onClick = onSeeAllClick) {
                            Text(
                                text = "See all",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FarmeraGreenPrimary
                            )
                        }
                    }

                    CategoryRow(
                        categories = categories,
                        selectedCategorySlug = selectedCategorySlug,
                        onSelectCategory = onSelectCategory
                    )
                }
            }

            // 3. "Fresh from farms" Section
            item {
                Column(modifier = Modifier.padding(bottom = 22.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Fresh from farms",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmeraTextPrimary
                            )
                            Text(
                                text = "Direct farmgate produce with zero middlemen markups",
                                fontSize = 11.sp,
                                color = FarmeraTextSecondary
                            )
                        }
                        TextButton(onClick = onSeeAllClick) {
                            Text(
                                text = "See all",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FarmeraGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(freshFromFarms, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                quantityInCart = getProductQuantity(product.id),
                                onCardClick = { onProductClick(product) },
                                onAddToCart = { onAddToCart(product) },
                                onIncrement = { onIncrement(product.id) },
                                onDecrement = { onDecrement(product.id) }
                            )
                        }
                    }
                }
            }

            // 4. "Harvesting this week" Section
            item {
                Column(modifier = Modifier.padding(bottom = 22.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Harvesting this week",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmeraTextPrimary
                            )
                            Text(
                                text = "Picked in Benue, Ogun and Kano within 48 hours",
                                fontSize = 11.sp,
                                color = FarmeraTextSecondary
                            )
                        }
                        TextButton(onClick = onSeeAllClick) {
                            Text(
                                text = "See all",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = FarmeraGreenPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(harvestingThisWeek, key = { it.id }) { product ->
                            ProductCard(
                                product = product,
                                quantityInCart = getProductQuantity(product.id),
                                onCardClick = { onProductClick(product) },
                                onAddToCart = { onAddToCart(product) },
                                onIncrement = { onIncrement(product.id) },
                                onDecrement = { onDecrement(product.id) }
                            )
                        }
                    }
                }
            }

            // 5. "Bulk Buys" Highlight Section for food businesses, resellers, restaurants
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(FarmeraMintCard)
                        .border(1.dp, Color(0xFFBBF7D0), RoundedCornerShape(16.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Bulk Buys for Businesses",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmeraTextPrimary
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(FarmeraGreenPrimary)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "SAVE UP TO 18%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Verified bulk supply of tubers, grains, peppers and oils for restaurants, caterers, boarding schools, and food resellers.",
                            fontSize = 13.sp,
                            color = FarmeraTextSecondary,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onSeeAllClick,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = FarmeraGreenPrimary,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("explore_bulk_buys_button")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Explore bulk buys",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
