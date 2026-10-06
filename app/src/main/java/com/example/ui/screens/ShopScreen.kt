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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Category
import com.example.model.Product
import com.example.ui.components.ProductCard
import com.example.ui.theme.FarmeraBorder
import com.example.ui.theme.FarmeraGreenDark
import com.example.ui.theme.FarmeraGreenPrimary
import com.example.ui.theme.FarmeraMintSurface
import com.example.ui.theme.FarmeraTextMuted
import com.example.ui.theme.FarmeraTextPrimary
import com.example.ui.theme.FarmeraTextSecondary

@Composable
fun ShopScreen(
    products: List<Product>,
    categories: List<Category>,
    selectedFilterChip: String,
    selectedCategorySlug: String?,
    getProductQuantity: (String) -> Int,
    onFilterChipSelect: (String) -> Unit,
    onCategorySlugSelect: (String?) -> Unit,
    onSearchClick: () -> Unit,
    onProductClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    onIncrement: (String) -> Unit,
    onDecrement: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // Dynamic Farmera categories + All + Bulk
    val categoryNames = categories.map { it.name }
    val filterChips = listOf("All") + categoryNames + listOf("Bulk")

    // Filter products based on selected chip and category slug
    val filteredProducts = products.filter { prod ->
        val matchesCategory = if (selectedCategorySlug != null) {
            prod.categorySlug.equals(selectedCategorySlug, ignoreCase = true) ||
            prod.category.equals(selectedCategorySlug, ignoreCase = true)
        } else true

        val matchesChip = when (selectedFilterChip) {
            "All" -> true
            "Bulk" -> prod.isBulkAvailable
            else -> {
                val cat = categories.find { it.name.equals(selectedFilterChip, ignoreCase = true) || it.slug.equals(selectedFilterChip, ignoreCase = true) }
                if (cat != null) {
                    prod.categorySlug.equals(cat.slug, ignoreCase = true) ||
                    prod.category.equals(cat.name, ignoreCase = true)
                } else true
            }
        }

        matchesCategory && matchesChip
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .testTag("shop_screen")
    ) {
        // Header Row: Title + Search Icon Trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Shop the market",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = FarmeraTextPrimary
                )
                Text(
                    text = "${filteredProducts.size} items from verified farm producers",
                    fontSize = 12.sp,
                    color = FarmeraTextSecondary
                )
            }

            IconButton(
                onClick = onSearchClick,
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFF9FAFB))
                    .border(1.dp, FarmeraBorder, RoundedCornerShape(10.dp))
                    .size(40.dp)
                    .testTag("shop_search_icon_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = FarmeraTextPrimary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Horizontal Filter Chips Row
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterChips.size) { index ->
                val chip = filterChips[index]
                val isSelected = when {
                    chip == "All" -> selectedCategorySlug == null && (selectedFilterChip == "All" || selectedFilterChip.isBlank())
                    chip == "Bulk" -> selectedFilterChip == "Bulk"
                    else -> {
                        val cat = categories.find { it.name == chip }
                        (cat != null && cat.slug.equals(selectedCategorySlug, ignoreCase = true)) ||
                        selectedFilterChip.equals(chip, ignoreCase = true)
                    }
                }

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        when (chip) {
                            "All" -> {
                                onFilterChipSelect("All")
                                onCategorySlugSelect(null)
                            }
                            "Bulk" -> {
                                onFilterChipSelect("Bulk")
                                onCategorySlugSelect(null)
                            }
                            else -> {
                                val cat = categories.find { it.name == chip }
                                onFilterChipSelect(chip)
                                onCategorySlugSelect(cat?.slug)
                            }
                        }
                    },
                    label = {
                        Text(
                            text = chip,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = FarmeraGreenPrimary,
                        selectedLabelColor = Color.White,
                        containerColor = Color(0xFFF9FAFB),
                        labelColor = FarmeraTextPrimary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) FarmeraGreenPrimary else FarmeraBorder
                    ),
                    shape = RoundedCornerShape(18.dp)
                )
            }
        }

        // Category filter active indicator if one is selected
        if (selectedCategorySlug != null) {
            val catName = categories.find { it.slug == selectedCategorySlug }?.name ?: selectedCategorySlug
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(FarmeraMintSurface)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Category: $catName",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = FarmeraGreenDark
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear category filter",
                            tint = FarmeraGreenDark,
                            modifier = Modifier
                                .size(14.dp)
                                .clickable {
                                    onCategorySlugSelect(null)
                                    onFilterChipSelect("All")
                                }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // 2-Column Responsive Product Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 120.dp),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (filteredProducts.isEmpty()) {
                item(span = { GridItemSpan(2) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No items match your filter",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = FarmeraTextPrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Try clearing the active category or filter chip.",
                                fontSize = 13.sp,
                                color = FarmeraTextSecondary
                            )
                        }
                    }
                }
            } else {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        quantityInCart = getProductQuantity(product.id),
                        onCardClick = { onProductClick(product) },
                        onAddToCart = { onAddToCart(product) },
                        onIncrement = { onIncrement(product.id) },
                        onDecrement = { onDecrement(product.id) },
                        cardWidth = 170.dp,
                        imageHeight = 145.dp
                    )
                }
            }
        }
    }
}
