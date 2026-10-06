package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.GoogleAuthResult
import com.example.data.FarmeraRepository
import com.example.data.NetworkFarmeraRepository
import com.example.data.SessionManager
import com.example.model.AddCartItemRequest
import com.example.model.ApiCart
import com.example.model.AuthResponse
import com.example.model.BannerSlide
import com.example.model.CartItem
import com.example.model.Category
import com.example.model.GoogleAuthRequest
import com.example.model.Product
import com.example.model.UpdateCartItemRequest
import com.example.model.UserProfile
import com.example.network.ApiClient
import com.example.network.AuthApiService
import com.example.network.CartApiService
import com.example.network.CartSseManager
import com.example.ui.components.FarmeraNavDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    ONBOARDING,
    MAIN,
    SEARCH,
    PRODUCT_DETAIL
}

data class FarmeraUiState(
    val currentScreen: AppScreen = AppScreen.SPLASH,
    val currentNavDestination: FarmeraNavDestination = FarmeraNavDestination.HOME,
    val previousScreens: List<AppScreen> = emptyList(),
    val selectedProduct: Product? = null,
    val selectedLocation: String = "Ajah, Lagos",
    val searchQuery: String = "",
    val searchResults: List<Product> = emptyList(),
    val recentSearches: List<String> = listOf("Yam", "Palm oil", "Garri", "Plantain", "Tomatoes"),
    val selectedCategorySlug: String? = null,
    val selectedFilterChip: String = "All", // "All", "Fresh Produce", "Pantry", "Bulk"
    val isBasketSheetOpen: Boolean = false,
    val cartItems: List<CartItem> = emptyList(),
    val allProducts: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val heroSlides: List<BannerSlide> = emptyList(),
    val freshFromFarms: List<Product> = emptyList(),
    val harvestingThisWeek: List<Product> = emptyList(),
    val bulkBuys: List<Product> = emptyList(),
    val isLoading: Boolean = false,
    val syncStatusText: String = "Synced across Farmera • Up to date",
    // Authentication State
    val authUser: UserProfile? = null,
    val isAuthenticated: Boolean = false,
    val isAuthLoading: Boolean = false,
    val authErrorMessage: String? = null
) {
    val totalCartItemsCount: Int
        get() = cartItems.sumOf { it.quantity }
}

class FarmeraViewModel(
    private val repository: FarmeraRepository = NetworkFarmeraRepository(),
    private val sessionManager: SessionManager? = null,
    private val authApiService: AuthApiService = ApiClient.authService,
    private val cartApiService: CartApiService = ApiClient.cartService
) : ViewModel() {

    private val _uiState = MutableStateFlow(FarmeraUiState())
    val uiState: StateFlow<FarmeraUiState> = _uiState.asStateFlow()

    private val cartSseManager = CartSseManager(
        onCartUpdated = { serverCart ->
            applyServerCart(serverCart)
        }
    )

    init {
        loadData()
        checkExistingSession()
    }

    fun loadData() {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                repository.getProducts().collectLatest { products ->
                    val dynamicCategories = deriveCategories(products)
                    val validCategorySlugs = dynamicCategories.map { it.slug }.toSet()

                    _uiState.update { state ->
                        // Automatically reset selectedCategorySlug to null / All if obsolete or not in live categories
                        val validatedCategorySlug = if (state.selectedCategorySlug != null && !validCategorySlugs.contains(state.selectedCategorySlug)) {
                            null
                        } else {
                            state.selectedCategorySlug
                        }

                        val validatedFilterChip = if (validatedCategorySlug == null && state.selectedFilterChip != "Bulk") {
                            "All"
                        } else if (validatedCategorySlug != null) {
                            dynamicCategories.find { it.slug == validatedCategorySlug }?.name ?: state.selectedFilterChip
                        } else {
                            state.selectedFilterChip
                        }

                        state.copy(
                            allProducts = products,
                            searchResults = products,
                            categories = dynamicCategories,
                            selectedCategorySlug = validatedCategorySlug,
                            selectedFilterChip = validatedFilterChip,
                            freshFromFarms = products.take(6),
                            harvestingThisWeek = products.filter { it.isHarvestingThisWeek }.ifEmpty { products.take(4) },
                            bulkBuys = products.filter { it.isBulkAvailable }.ifEmpty { products.take(4) },
                            isLoading = false,
                            syncStatusText = "Synced with Farmera Backend • ${products.size} items"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false) }
            }
        }
        viewModelScope.launch {
            repository.getHeroSlides().collectLatest { slides ->
                _uiState.update { it.copy(heroSlides = slides) }
            }
        }
    }

    private fun deriveCategories(products: List<Product>): List<Category> {
        val defaultCategories = listOf(
            Category("cat_tubers", "Tubers & Roots", "tubers", "yam", 0xFFFFF1E6),
            Category("cat_fruits", "Fruits", "fruits", "orange", 0xFFFFFBEB),
            Category("cat_vegetables", "Vegetables", "vegetables", "pepper", 0xFFECFDF5),
            Category("cat_grains", "Grains & Beans", "grains", "beans", 0xFFFEF3C7),
            Category("cat_oils", "Oils", "oils", "palmoil", 0xFFFFF7ED),
            Category("cat_pantry", "Pantry Staples", "pantry", "garri", 0xFFFDF4FF)
        )

        if (products.isEmpty()) return defaultCategories

        val distinctPairs = products.map { it.category to it.categorySlug }.distinctBy { it.second }
        return distinctPairs.map { (name, slug) ->
            val (itemKey, tint) = when (slug.lowercase()) {
                "tubers" -> "yam" to 0xFFFFF1E6
                "fruits" -> "orange" to 0xFFFFFBEB
                "vegetables" -> "pepper" to 0xFFECFDF5
                "grains" -> "beans" to 0xFFFEF3C7
                "oils" -> "palmoil" to 0xFFFFF7ED
                "pantry" -> "garri" to 0xFFFDF4FF
                else -> "crate" to 0xFFF0FDF4
            }
            Category(
                id = "cat_$slug",
                name = name,
                slug = slug,
                itemKey = itemKey,
                bgTintHex = tint
            )
        }
    }

    // ==========================================
    // AUTHENTICATION LIFECYCLE
    // ==========================================

    fun checkExistingSession() {
        val token = sessionManager?.getToken()
        if (token.isNullOrBlank()) {
            _uiState.update { it.copy(isAuthenticated = false, authUser = null) }
            return
        }

        // Restore cached user profile immediately for offline/warm launch
        val cachedUser = sessionManager?.getUser()
        if (cachedUser != null) {
            _uiState.update { it.copy(isAuthenticated = true, authUser = cachedUser) }
        }

        // Single source of truth: sync server cart immediately and connect real-time SSE listener
        syncCartFromServer(token)
        cartSseManager.startListening(viewModelScope, token)

        // Validate token with backend GET /api/auth/me
        viewModelScope.launch {
            try {
                val response = authApiService.getCurrentUser("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val validatedUser = body.user ?: cachedUser
                    if (validatedUser != null) {
                        sessionManager?.saveSession(token, validatedUser)
                        _uiState.update {
                            it.copy(
                                isAuthenticated = true,
                                authUser = validatedUser,
                                authErrorMessage = null
                            )
                        }
                    } else {
                        handleSessionExpired()
                    }
                } else if (response.code() in listOf(401, 403)) {
                    // Session expired or revoked
                    handleSessionExpired()
                }
            } catch (e: Exception) {
                // If offline or network timeout on launch, retain cached user session
            }
        }
    }

    fun handleGoogleSignInResult(result: GoogleAuthResult) {
        when (result) {
            is GoogleAuthResult.Success -> {
                exchangeGoogleTokenForSession(result.idToken)
            }
            is GoogleAuthResult.Cancelled -> {
                _uiState.update { it.copy(isAuthLoading = false) }
            }
            is GoogleAuthResult.Failure -> {
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = result.message
                    )
                }
            }
        }
    }

    fun setAuthLoading(loading: Boolean) {
        _uiState.update { it.copy(isAuthLoading = loading, authErrorMessage = null) }
    }

    fun dismissAuthError() {
        _uiState.update { it.copy(authErrorMessage = null) }
    }

    fun exchangeGoogleTokenForSession(idToken: String) {
        _uiState.update { it.copy(isAuthLoading = true, authErrorMessage = null) }
        viewModelScope.launch {
            try {
                val request = GoogleAuthRequest(idToken = idToken)
                val response = authApiService.googleAuth(request)
                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!
                    val sessionToken = body.resolvedToken
                    if (!sessionToken.isNullOrBlank()) {
                        val user = body.user ?: UserProfile(
                            id = "usr_google_${System.currentTimeMillis()}",
                            email = "buyer@farmera.ng",
                            name = "Farmera Buyer"
                        )
                        sessionManager?.saveSession(sessionToken, user)
                        _uiState.update {
                            it.copy(
                                isAuthenticated = true,
                                authUser = user,
                                isAuthLoading = false,
                                authErrorMessage = null
                            )
                        }
                        // Migrate any local cart items to server cart if present
                        val localItems = _uiState.value.cartItems
                        if (localItems.isNotEmpty()) {
                            viewModelScope.launch {
                                localItems.forEach { local ->
                                    try {
                                        cartApiService.addItem(
                                            "Bearer $sessionToken",
                                            AddCartItemRequest(productId = local.product.slug, quantity = local.quantity)
                                        )
                                    } catch (_: Exception) {}
                                }
                                syncCartFromServer(sessionToken)
                            }
                        } else {
                            syncCartFromServer(sessionToken)
                        }
                        // Start real-time Server-Sent Events listener
                        cartSseManager.startListening(viewModelScope, sessionToken)
                    } else if (body.user != null) {
                        val fallbackToken = "session_${System.currentTimeMillis()}"
                        sessionManager?.saveSession(fallbackToken, body.user)
                        _uiState.update {
                            it.copy(
                                isAuthenticated = true,
                                authUser = body.user,
                                isAuthLoading = false,
                                authErrorMessage = null
                            )
                        }
                        syncCartFromServer(fallbackToken)
                        cartSseManager.startListening(viewModelScope, fallbackToken)
                    } else {
                        val err = body.error ?: body.message ?: "Authentication failed on backend."
                        _uiState.update {
                            it.copy(isAuthLoading = false, authErrorMessage = err)
                        }
                    }
                } else {
                    val friendlyMsg = when (response.code()) {
                        404 -> "Farmera backend authentication endpoint is preparing deployment. Sourcing services remain active."
                        401 -> "Google authentication could not be validated by backend server."
                        else -> "Unable to complete sign-in (Error ${response.code()}). Please try again."
                    }
                    _uiState.update {
                        it.copy(isAuthLoading = false, authErrorMessage = friendlyMsg)
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isAuthLoading = false,
                        authErrorMessage = "Could not reach Farmera servers. Please check your internet connection."
                    )
                }
            }
        }
    }

    fun signOut() {
        val token = sessionManager?.getToken()
        _uiState.update { it.copy(isAuthLoading = true) }
        cartSseManager.stopListening()
        viewModelScope.launch {
            if (!token.isNullOrBlank()) {
                try {
                    authApiService.logout("Bearer $token")
                } catch (e: Exception) {
                    // Best effort server logout
                }
            }
            sessionManager?.clearSession()
            _uiState.update {
                it.copy(
                    isAuthenticated = false,
                    authUser = null,
                    cartItems = emptyList(),
                    isAuthLoading = false,
                    authErrorMessage = null
                )
            }
        }
    }

    private fun handleSessionExpired() {
        cartSseManager.stopListening()
        sessionManager?.clearSession()
        _uiState.update {
            it.copy(
                isAuthenticated = false,
                authUser = null,
                cartItems = emptyList(),
                authErrorMessage = "Your session has expired. Please sign in again."
            )
        }
    }

    // ==========================================
    // NAVIGATION & BROWSING
    // ==========================================

    fun finishSplash() {
        _uiState.update { it.copy(currentScreen = AppScreen.ONBOARDING) }
    }

    fun completeOnboarding() {
        _uiState.update { it.copy(currentScreen = AppScreen.MAIN) }
    }

    fun selectNavDestination(destination: FarmeraNavDestination) {
        _uiState.update {
            it.copy(
                currentNavDestination = destination,
                currentScreen = AppScreen.MAIN
            )
        }
    }

    fun navigateToSearch() {
        _uiState.update {
            it.copy(
                previousScreens = it.previousScreens + it.currentScreen,
                currentScreen = AppScreen.SEARCH
            )
        }
    }

    fun navigateToProductDetail(product: Product) {
        _uiState.update {
            it.copy(
                selectedProduct = product,
                previousScreens = it.previousScreens + it.currentScreen,
                currentScreen = AppScreen.PRODUCT_DETAIL
            )
        }
        viewModelScope.launch {
            try {
                repository.getProductById(product.id).collectLatest { updated ->
                    if (updated != null) {
                        _uiState.update { it.copy(selectedProduct = updated) }
                    }
                }
            } catch (e: Exception) {
                // Keep selected product if detail call fails
            }
        }
    }

    fun refreshCatalogue() {
        (repository as? NetworkFarmeraRepository)?.invalidateCache()
        loadData()
    }

    fun navigateBack(): Boolean {
        val currentState = _uiState.value
        if (currentState.isBasketSheetOpen) {
            closeBasketSheet()
            return true
        }

        if (currentState.previousScreens.isNotEmpty()) {
            val lastScreen = currentState.previousScreens.last()
            val remainingScreens = currentState.previousScreens.dropLast(1)
            _uiState.update {
                it.copy(
                    currentScreen = lastScreen,
                    previousScreens = remainingScreens
                )
            }
            return true
        }

        if (currentState.currentScreen == AppScreen.MAIN && currentState.currentNavDestination != FarmeraNavDestination.HOME) {
            _uiState.update { it.copy(currentNavDestination = FarmeraNavDestination.HOME) }
            return true
        }

        return false
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        viewModelScope.launch {
            repository.searchProducts(query).collectLatest { results ->
                _uiState.update { it.copy(searchResults = results) }
            }
        }
    }

    fun selectRecentSearch(query: String) {
        updateSearchQuery(query)
    }

    fun selectCategoryFilter(categorySlug: String?) {
        _uiState.update { state ->
            val newSlug = if (state.selectedCategorySlug == categorySlug) null else categorySlug
            val matchingCat = state.categories.find { it.slug == newSlug }
            val newChip = if (newSlug == null) "All" else (matchingCat?.name ?: "All")
            state.copy(
                selectedCategorySlug = newSlug,
                selectedFilterChip = newChip,
                currentNavDestination = FarmeraNavDestination.SHOP,
                currentScreen = AppScreen.MAIN
            )
        }
    }

    fun setFilterChip(chip: String) {
        _uiState.update { state ->
            val newSlug = when (chip) {
                "All", "Bulk" -> null
                else -> state.categories.find { it.name.equals(chip, ignoreCase = true) || it.slug.equals(chip, ignoreCase = true) }?.slug
            }
            state.copy(
                selectedFilterChip = chip,
                selectedCategorySlug = newSlug
            )
        }
    }

    fun setDeliveryLocation(location: String) {
        _uiState.update { it.copy(selectedLocation = location) }
    }

    fun openBasketSheet() {
        _uiState.update { it.copy(isBasketSheetOpen = true) }
    }

    fun closeBasketSheet() {
        _uiState.update { it.copy(isBasketSheetOpen = false) }
    }

    fun goToCartFromSheet() {
        _uiState.update {
            it.copy(
                isBasketSheetOpen = false,
                currentNavDestination = FarmeraNavDestination.CART,
                currentScreen = AppScreen.MAIN
            )
        }
    }

    // ==========================================
    // CART STATE (SHARED WEB <-> MOBILE NEON CART)
    // ==========================================

    fun syncCartFromServer(token: String) {
        viewModelScope.launch {
            try {
                val response = cartApiService.getCart("Bearer $token")
                if (response.isSuccessful && response.body()?.cart != null) {
                    applyServerCart(response.body()!!.cart!!)
                } else if (response.code() == 401) {
                    handleSessionExpired()
                }
            } catch (_: Exception) {
                // Network failure: preserve last valid cart view
            }
        }
    }

    fun applyServerCart(apiCart: ApiCart) {
        val domainItems = apiCart.items?.map { it.toDomainCartItem(_uiState.value.allProducts) } ?: emptyList()
        _uiState.update { state ->
            val totalCount = domainItems.sumOf { it.quantity }
            state.copy(
                cartItems = domainItems,
                syncStatusText = "Server cart synced • $totalCount items",
                isBasketSheetOpen = if (domainItems.isEmpty()) false else state.isBasketSheetOpen
            )
        }
    }

    fun addToCart(product: Product, isBulk: Boolean = false, quantity: Int = 1) {
        if (quantity <= 0) return
        val token = sessionManager?.getToken()
        if (!token.isNullOrBlank()) {
            viewModelScope.launch {
                try {
                    val request = AddCartItemRequest(productId = product.slug, quantity = quantity)
                    val response = cartApiService.addItem("Bearer $token", request)
                    if (response.isSuccessful && response.body()?.cart != null) {
                        applyServerCart(response.body()!!.cart!!)
                    } else if (response.code() == 401) {
                        handleSessionExpired()
                    }
                } catch (_: Exception) {
                    // Retain current cart state
                }
            }
        } else {
            _uiState.update { state ->
                val existing = state.cartItems.find { it.product.id == product.id || it.product.slug == product.slug }
                val updatedItems = if (existing != null) {
                    state.cartItems.map {
                        if (it.product.id == product.id || it.product.slug == product.slug) it.copy(quantity = it.quantity + quantity) else it
                    }
                } else {
                    state.cartItems + CartItem(product = product, quantity = quantity, isBulk = isBulk)
                }
                state.copy(cartItems = updatedItems)
            }
        }
    }

    fun incrementCartItem(productId: String) {
        val token = sessionManager?.getToken()
        val currentItem = _uiState.value.cartItems.find {
            it.product.id == productId || it.product.slug == productId
        } ?: return

        if (!token.isNullOrBlank()) {
            viewModelScope.launch {
                try {
                    val response = if (currentItem.serverItemId != null) {
                        cartApiService.updateItem(
                            "Bearer $token",
                            currentItem.serverItemId,
                            UpdateCartItemRequest(quantity = currentItem.quantity + 1)
                        )
                    } else {
                        cartApiService.addItem(
                            "Bearer $token",
                            AddCartItemRequest(productId = currentItem.product.slug, quantity = 1)
                        )
                    }
                    if (response.isSuccessful && response.body()?.cart != null) {
                        applyServerCart(response.body()!!.cart!!)
                    } else if (response.code() == 401) {
                        handleSessionExpired()
                    }
                } catch (_: Exception) {
                    // Retain current cart state
                }
            }
        } else {
            _uiState.update { state ->
                val updatedItems = state.cartItems.map {
                    if (it.product.id == productId || it.product.slug == productId) it.copy(quantity = it.quantity + 1) else it
                }
                state.copy(cartItems = updatedItems)
            }
        }
    }

    fun decrementCartItem(productId: String) {
        val token = sessionManager?.getToken()
        val currentItem = _uiState.value.cartItems.find {
            it.product.id == productId || it.product.slug == productId
        } ?: return

        if (!token.isNullOrBlank()) {
            viewModelScope.launch {
                try {
                    val response = if (currentItem.quantity > 1 && currentItem.serverItemId != null) {
                        cartApiService.updateItem(
                            "Bearer $token",
                            currentItem.serverItemId,
                            UpdateCartItemRequest(quantity = currentItem.quantity - 1)
                        )
                    } else if (currentItem.serverItemId != null) {
                        cartApiService.removeItem("Bearer $token", currentItem.serverItemId)
                    } else {
                        null
                    }

                    if (response != null && response.isSuccessful && response.body()?.cart != null) {
                        applyServerCart(response.body()!!.cart!!)
                    } else if (response?.code() == 401) {
                        handleSessionExpired()
                    }
                } catch (_: Exception) {
                    // Retain current cart state
                }
            }
        } else {
            _uiState.update { state ->
                val updatedItems = state.cartItems.mapNotNull { item ->
                    if (item.product.id == productId || item.product.slug == productId) {
                        if (item.quantity > 1) item.copy(quantity = item.quantity - 1) else null
                    } else item
                }
                val willBeEmpty = updatedItems.isEmpty()
                state.copy(
                    cartItems = updatedItems,
                    isBasketSheetOpen = if (willBeEmpty) false else state.isBasketSheetOpen
                )
            }
        }
    }

    fun removeCartItem(productId: String) {
        val token = sessionManager?.getToken()
        val currentItem = _uiState.value.cartItems.find {
            it.product.id == productId || it.product.slug == productId
        }

        if (!token.isNullOrBlank() && currentItem?.serverItemId != null) {
            viewModelScope.launch {
                try {
                    val response = cartApiService.removeItem("Bearer $token", currentItem.serverItemId)
                    if (response.isSuccessful && response.body()?.cart != null) {
                        applyServerCart(response.body()!!.cart!!)
                    } else if (response.code() == 401) {
                        handleSessionExpired()
                    }
                } catch (_: Exception) {
                    // Retain current cart state
                }
            }
        } else {
            _uiState.update { state ->
                val updatedItems = state.cartItems.filterNot { it.product.id == productId || it.product.slug == productId }
                val willBeEmpty = updatedItems.isEmpty()
                state.copy(
                    cartItems = updatedItems,
                    isBasketSheetOpen = if (willBeEmpty) false else state.isBasketSheetOpen
                )
            }
        }
    }

    fun clearCart() {
        val token = sessionManager?.getToken()
        if (!token.isNullOrBlank()) {
            viewModelScope.launch {
                try {
                    val response = cartApiService.clearCart("Bearer $token")
                    if (response.isSuccessful && response.body()?.cart != null) {
                        applyServerCart(response.body()!!.cart!!)
                    } else if (response.code() == 401) {
                        handleSessionExpired()
                    }
                } catch (_: Exception) {
                    // Retain current cart state
                }
            }
        } else {
            _uiState.update { it.copy(cartItems = emptyList(), isBasketSheetOpen = false) }
        }
    }

    fun getProductQuantity(productId: String): Int {
        return _uiState.value.cartItems.find {
            it.product.id == productId || it.product.slug == productId
        }?.quantity ?: 0
    }

    override fun onCleared() {
        super.onCleared()
        cartSseManager.stopListening()
    }
}
