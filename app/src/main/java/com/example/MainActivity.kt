package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.auth.GoogleAuthManager
import com.example.data.MockFarmeraRepository
import com.example.data.NetworkFarmeraRepository
import com.example.data.SessionManager
import com.example.model.BannerSlide
import com.example.model.Category
import com.example.model.Product
import com.example.network.ApiClient
import com.example.ui.components.BasketBottomSheet
import com.example.ui.components.FarmeraBottomNav
import com.example.ui.components.FarmeraNavDestination
import com.example.ui.components.StickyBasketBar
import com.example.ui.screens.CartScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PlaceholderScreen
import com.example.ui.screens.ProductDetailScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.screens.ShopScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.theme.FarmeraBorderLight
import com.example.ui.theme.FarmeraGreenDark
import com.example.ui.theme.FarmeraGreenPrimary
import com.example.ui.theme.FarmeraTextPrimary
import com.example.ui.theme.FarmeraTextSecondary
import com.example.ui.theme.FarmeraTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.FarmeraViewModel
import kotlinx.coroutines.launch

class FarmeraViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return FarmeraViewModel(
            repository = NetworkFarmeraRepository(),
            sessionManager = SessionManager(context.applicationContext),
            authApiService = ApiClient.authService,
            cartApiService = ApiClient.cartService
        ) as T
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FarmeraTheme {
                FarmeraApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmeraApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val viewModel: FarmeraViewModel = viewModel(
        factory = FarmeraViewModelFactory(context)
    )
    val googleAuthManager = remember { GoogleAuthManager(context) }

    val uiState by viewModel.uiState.collectAsState()
    val totalCartItemsCount = uiState.totalCartItemsCount
    var isLocationSheetOpen by remember { mutableStateOf(false) }

    // Lagos delivery hub locations
    val lagosLocations = listOf(
        "Ajah, Lagos",
        "Lekki Phase 1, Lagos",
        "Victoria Island, Lagos",
        "Ikoyi, Lagos",
        "Ikeja GRA, Lagos",
        "Yaba / Mainland, Lagos",
        "Surulere, Lagos"
    )

    // Handle Hardware Back navigation gracefully
    BackHandler(
        enabled = uiState.currentScreen != AppScreen.SPLASH
    ) {
        if (!viewModel.navigateBack()) {
            // Let the system back action handle app exit
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("farmera_app_root"),
        containerColor = Color.White,
        bottomBar = {
            if (uiState.currentScreen == AppScreen.MAIN) {
                Column {
                    // Show sticky basket bar only on Home/Shop when cart has items
                    if (uiState.currentNavDestination != FarmeraNavDestination.CART) {
                        StickyBasketBar(
                            cartItems = uiState.cartItems,
                            onViewBasket = { viewModel.openBasketSheet() }
                        )
                    }

                    // Bottom Navigation Bar
                    FarmeraBottomNav(
                        currentDestination = uiState.currentNavDestination,
                        cartItemCount = totalCartItemsCount,
                        onNavigate = { destination ->
                            viewModel.selectNavDestination(destination)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    bottom = if (uiState.currentScreen == AppScreen.MAIN) innerPadding.calculateBottomPadding() else 0.dp
                )
        ) {
            when (uiState.currentScreen) {
                AppScreen.SPLASH -> {
                    SplashScreen(
                        onSplashFinished = { viewModel.finishSplash() }
                    )
                }

                AppScreen.ONBOARDING -> {
                    OnboardingScreen(
                        onComplete = { viewModel.completeOnboarding() }
                    )
                }

                AppScreen.SEARCH -> {
                    SearchScreen(
                        query = uiState.searchQuery,
                        searchResults = uiState.searchResults,
                        recentSearches = uiState.recentSearches,
                        onQueryChange = { viewModel.updateSearchQuery(it) },
                        onSelectRecent = { viewModel.selectRecentSearch(it) },
                        onBackClick = { viewModel.navigateBack() },
                        getProductQuantity = { viewModel.getProductQuantity(it) },
                        onProductClick = { viewModel.navigateToProductDetail(it) },
                        onAddToCart = { viewModel.addToCart(it) },
                        onIncrement = { viewModel.incrementCartItem(it) },
                        onDecrement = { viewModel.decrementCartItem(it) }
                    )
                }

                AppScreen.PRODUCT_DETAIL -> {
                    uiState.selectedProduct?.let { product ->
                        ProductDetailScreen(
                            product = product,
                            quantityInCart = viewModel.getProductQuantity(product.id),
                            cartItemCount = totalCartItemsCount,
                            onBackClick = { viewModel.navigateBack() },
                            onAddToCart = { prod, isBulk, count -> viewModel.addToCart(prod, isBulk, count) },
                            onOpenBasket = { viewModel.openBasketSheet() }
                        )
                    }
                }

                AppScreen.MAIN -> {
                    when (uiState.currentNavDestination) {
                        FarmeraNavDestination.HOME -> {
                            HomeScreen(
                                selectedLocation = uiState.selectedLocation,
                                cartItemCount = totalCartItemsCount,
                                heroSlides = uiState.heroSlides,
                                categories = uiState.categories,
                                selectedCategorySlug = uiState.selectedCategorySlug,
                                freshFromFarms = uiState.freshFromFarms,
                                harvestingThisWeek = uiState.harvestingThisWeek,
                                bulkBuys = uiState.bulkBuys,
                                getProductQuantity = { viewModel.getProductQuantity(it) },
                                onLocationClick = { isLocationSheetOpen = true },
                                onBasketClick = { viewModel.openBasketSheet() },
                                onSearchClick = { viewModel.navigateToSearch() },
                                onHeroCtaClick = { slide ->
                                    if (slide.id == "slide_2") {
                                        viewModel.setFilterChip("Bulk")
                                    }
                                    viewModel.selectNavDestination(FarmeraNavDestination.SHOP)
                                },
                                onSelectCategory = { category ->
                                    viewModel.selectCategoryFilter(category.slug)
                                },
                                onSeeAllClick = {
                                    viewModel.selectNavDestination(FarmeraNavDestination.SHOP)
                                },
                                onProductClick = { viewModel.navigateToProductDetail(it) },
                                onAddToCart = { viewModel.addToCart(it) },
                                onIncrement = { viewModel.incrementCartItem(it) },
                                onDecrement = { viewModel.decrementCartItem(it) }
                            )
                        }

                        FarmeraNavDestination.SHOP -> {
                            ShopScreen(
                                products = uiState.allProducts,
                                categories = uiState.categories,
                                selectedFilterChip = uiState.selectedFilterChip,
                                selectedCategorySlug = uiState.selectedCategorySlug,
                                getProductQuantity = { viewModel.getProductQuantity(it) },
                                onFilterChipSelect = { viewModel.setFilterChip(it) },
                                onCategorySlugSelect = { viewModel.selectCategoryFilter(it) },
                                onSearchClick = { viewModel.navigateToSearch() },
                                onProductClick = { viewModel.navigateToProductDetail(it) },
                                onAddToCart = { viewModel.addToCart(it) },
                                onIncrement = { viewModel.incrementCartItem(it) },
                                onDecrement = { viewModel.decrementCartItem(it) }
                            )
                        }

                        FarmeraNavDestination.CART -> {
                            CartScreen(
                                cartItems = uiState.cartItems,
                                syncStatusText = uiState.syncStatusText,
                                onIncrement = { viewModel.incrementCartItem(it) },
                                onDecrement = { viewModel.decrementCartItem(it) },
                                onRemove = { viewModel.removeCartItem(it) },
                                onBrowseMarket = {
                                    viewModel.selectNavDestination(FarmeraNavDestination.SHOP)
                                }
                            )
                        }

                        FarmeraNavDestination.ORDERS -> {
                            PlaceholderScreen(destination = FarmeraNavDestination.ORDERS)
                        }

                        FarmeraNavDestination.PROFILE -> {
                            ProfileScreen(
                                isAuthenticated = uiState.isAuthenticated,
                                user = uiState.authUser,
                                isLoading = uiState.isAuthLoading,
                                errorMessage = uiState.authErrorMessage,
                                onGoogleSignInClick = {
                                    coroutineScope.launch {
                                        viewModel.setAuthLoading(true)
                                        val result = googleAuthManager.signInWithGoogle()
                                        viewModel.handleGoogleSignInResult(result)
                                    }
                                },
                                onSignOutClick = {
                                    viewModel.signOut()
                                },
                                onDismissError = {
                                    viewModel.dismissAuthError()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Draggable Mini Basket Bottom Sheet matching iPhone 14 & 15 Pro Max - 16.png
    BasketBottomSheet(
        isOpen = uiState.isBasketSheetOpen,
        cartItems = uiState.cartItems,
        onDismiss = { viewModel.closeBasketSheet() },
        onIncrement = { viewModel.incrementCartItem(it) },
        onDecrement = { viewModel.decrementCartItem(it) },
        onGoToCart = { viewModel.goToCartFromSheet() }
    )

    // Delivery Location Selector Bottom Sheet
    if (isLocationSheetOpen) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { isLocationSheetOpen = false },
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
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = FarmeraGreenPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Select Delivery Hub in Lagos",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = FarmeraTextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                LazyColumn {
                    items(lagosLocations) { loc ->
                        val isSelected = loc == uiState.selectedLocation
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDeliveryLocation(loc)
                                    isLocationSheetOpen = false
                                }
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = loc,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) FarmeraGreenDark else FarmeraTextPrimary
                            )
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = FarmeraGreenPrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        HorizontalDivider(color = FarmeraBorderLight)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}
