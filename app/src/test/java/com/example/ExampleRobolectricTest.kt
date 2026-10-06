package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.auth.GoogleAuthManager
import com.example.auth.GoogleAuthResult
import com.example.data.MockFarmeraRepository
import com.example.data.NetworkFarmeraRepository
import com.example.data.SessionManager
import com.example.model.AddCartItemRequest
import com.example.model.ApiCart
import com.example.model.ApiCartItem
import com.example.model.ApiCartResponse
import com.example.model.ApiProductItem
import com.example.model.ApiProductSpec
import com.example.model.AuthResponse
import com.example.model.UpdateCartItemRequest
import com.example.model.UserProfile
import com.example.network.ApiClient
import com.example.network.ApiConfig
import com.example.network.CartSseManager
import com.example.ui.viewmodel.FarmeraViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Farmera", appName)
    }

    @Test
    fun `apiConfig baseUrl and product endpoints are strictly configured`() {
        assertEquals("https://aperture-co-646783647451.europe-west2.run.app", ApiConfig.BASE_URL)
        assertEquals("api/auth/google", ApiConfig.AUTH_GOOGLE)
        assertEquals("api/auth/me", ApiConfig.AUTH_ME)
        assertEquals("api/auth/logout", ApiConfig.AUTH_LOGOUT)
        assertEquals("api/products", ApiConfig.PRODUCTS)
        assertEquals("api/products/{slug}", ApiConfig.PRODUCT_DETAIL)
    }

    @Test
    fun `apiConfig has exact configured Google Web Application Client ID`() {
        val expectedClientId = "668764933589-mmede6d874tpcfil4nu1t8rjt4209le9.apps.googleusercontent.com"
        assertEquals(expectedClientId, ApiConfig.googleWebClientId)
        assertTrue(ApiConfig.googleWebClientId.isNotBlank())
        assertTrue(ApiConfig.googleWebClientId.endsWith(".apps.googleusercontent.com"))
        assertNotEquals("646783647451-server.apps.googleusercontent.com", ApiConfig.googleWebClientId)
    }

    @Test
    fun `apiProductItem maps correctly to domain product`() {
        val apiItem = ApiProductItem(
            id = "benue-puna-yam",
            slug = "benue-puna-yam",
            name = "Benue Puna Yam",
            subtitle = "Direct farm harvest",
            edition = "Emmanuel Farms",
            category = "Tubers & Roots",
            categoryLabel = "Tubers & Roots",
            price = 18500.0,
            rating = 4.9,
            reviewCount = 38,
            tag = "DIRECT FROM BENUE",
            stockQuantity = 45,
            inStock = true,
            description = "Premium mature white puna yams from Gboko, Benue State.",
            images = listOf("https://images.unsplash.com/photo-1596755094514-f87e34085b2c"),
            specs = listOf(
                ApiProductSpec(label = "ORIGIN", value = "Gboko, Benue State"),
                ApiProductSpec(label = "UNIT", value = "10 large tubers (~25-30kg)")
            )
        )

        val domainProduct = apiItem.toDomainProduct()
        assertEquals("benue-puna-yam", domainProduct.id)
        assertEquals("benue-puna-yam", domainProduct.slug)
        assertEquals("Benue Puna Yam", domainProduct.name)
        assertEquals("Tubers & Roots", domainProduct.category)
        assertEquals("tubers", domainProduct.categorySlug)
        assertEquals(18500, domainProduct.price)
        assertEquals("₦18,500", domainProduct.formattedPrice)
        assertEquals("10 large tubers (~25-30kg)", domainProduct.unit)
        assertEquals("Gboko, Benue State", domainProduct.sourceLocation)
        assertEquals("yam", domainProduct.iconKey)
        assertTrue(domainProduct.isHarvestingThisWeek)
        assertTrue(domainProduct.isBulkAvailable)
        assertEquals("DIRECT FROM BENUE", domainProduct.badgeText)
        assertEquals(R.drawable.prod_benue_yam, domainProduct.imageModel)
        assertNotNull(domainProduct.imageUrl)
    }

    @Test
    fun `productImageResolver maps every Farmera product to authentic produce photography`() {
        // Corrected Nigerian produce items
        assertEquals(R.drawable.prod_benue_yam, com.example.model.ProductImageResolver.resolveImage("benue-puna-yam"))
        assertEquals(R.drawable.prod_brown_beans, com.example.model.ProductImageResolver.resolveImage("niger-brown-beans"))
        assertEquals(R.drawable.prod_red_palm_oil, com.example.model.ProductImageResolver.resolveImage("abia-red-palm-oil"))
        assertEquals(R.drawable.prod_yellow_garri, com.example.model.ProductImageResolver.resolveImage("edo-yellow-garri"))
        assertEquals(R.drawable.prod_white_garri, com.example.model.ProductImageResolver.resolveImage("enugu-white-garri"))
        assertEquals(R.drawable.prod_ogun_plantain, com.example.model.ProductImageResolver.resolveImage("ogun-plantain"))

        // Preserved verified produce photos
        assertTrue(com.example.model.ProductImageResolver.resolveImage("kano-tomatoes").toString().contains("photo-1592924357228"))
        assertTrue(com.example.model.ProductImageResolver.resolveImage("kano-red-onions").toString().contains("photo-1618512496248"))
        assertTrue(com.example.model.ProductImageResolver.resolveImage("fresh-pepper").toString().contains("photo-1588252303782"))
        assertTrue(com.example.model.ProductImageResolver.resolveImage("kaduna-maize").toString().contains("photo-1551754655"))
        assertTrue(com.example.model.ProductImageResolver.resolveImage("sweet-oranges").toString().contains("photo-1611080626919"))
        assertTrue(com.example.model.ProductImageResolver.resolveImage("avocado-pear").toString().contains("photo-1523049673857"))

        // Ensure all 12 products have dedicated mappings
        val slugs = listOf(
            "benue-puna-yam", "niger-brown-beans", "abia-red-palm-oil",
            "edo-yellow-garri", "enugu-white-garri", "ogun-plantain",
            "kano-tomatoes", "kano-red-onions", "fresh-pepper",
            "kaduna-maize", "sweet-oranges", "avocado-pear"
        )
        slugs.forEach { slug ->
            assertTrue("Slug $slug should have dedicated image", com.example.model.ProductImageResolver.hasDedicatedImage(slug))
        }
    }

    @Test
    fun `live backend API returns 12 agricultural products and parses successfully`() = runBlocking {
        val response = ApiClient.productService.getProducts()
        assertTrue("Live GET /api/products response should be successful", response.isSuccessful)
        val body = response.body()
        assertNotNull(body)
        assertEquals(true, body?.ok)
        val products = body?.products
        assertNotNull(products)
        assertEquals("Expected 12 products from backend", 12, products?.size)

        // Confirm it returns Farmera agricultural products
        assertTrue(products!!.any { it.slug == "benue-puna-yam" })
        assertTrue(products.any { it.slug == "kaduna-maize" })
        assertTrue(products.any { it.slug == "ogun-plantain" })
        assertTrue(products.any { it.slug == "kano-tomatoes" })

        // Ensure zero camera products
        assertFalse(products.any { it.slug == "noma-r4" })
        assertFalse(products.any { it.slug == "vela-m6" })
    }

    @Test
    fun `live backend API returns product detail by slug for agricultural product`() = runBlocking {
        val response = ApiClient.productService.getProductBySlug("benue-puna-yam")
        assertTrue("Live GET /api/products/benue-puna-yam should be successful", response.isSuccessful)
        val body = response.body()
        assertNotNull(body)
        assertEquals(true, body?.ok)
        val product = body?.product
        assertNotNull(product)
        assertEquals("benue-puna-yam", product?.slug)
        assertEquals("Benue Puna Yam", product?.name)
        assertEquals("Tubers & Roots", product?.category)
    }

    @Test
    fun `network repository loads and caches 12 live products with Farmera categories`() = runBlocking {
        val repo = NetworkFarmeraRepository()
        val products = repo.getProducts().first()
        assertTrue("Repository should yield products", products.isNotEmpty())
        assertEquals(12, products.size)

        // Search test
        val searchResults = repo.searchProducts("yam").first()
        assertTrue(searchResults.any { it.name.contains("Yam", ignoreCase = true) })

        // Category test
        val categories = repo.getCategories().first()
        assertTrue(categories.isNotEmpty())
        val categoryNames = categories.map { it.name }
        assertTrue(categoryNames.contains("Tubers & Roots"))
        assertTrue(categoryNames.contains("Fruits"))
        assertTrue(categoryNames.contains("Vegetables"))

        // Ensure zero Aperture categories
        assertFalse(categoryNames.any { it.contains("Camera", ignoreCase = true) })
        assertFalse(categoryNames.any { it.contains("Lens", ignoreCase = true) })
    }

    @Test
    fun `viewModel automatically clears obsolete category filter`() = runBlocking {
        val mockRepo = MockFarmeraRepository()
        val viewModel = FarmeraViewModel(repository = mockRepo)

        // Simulate an obsolete filter
        viewModel.selectCategoryFilter("obsolete-unknown-category")
        assertEquals("obsolete-unknown-category", viewModel.uiState.value.selectedCategorySlug)

        // Reload data - must automatically reset invalid category filter to null / All
        viewModel.loadData()
        assertNull(viewModel.uiState.value.selectedCategorySlug)
        assertEquals("All", viewModel.uiState.value.selectedFilterChip)
    }

    @Test
    fun `googleAuthManager reports friendly failure when client ID is not configured`() = runBlocking {
        val original = ApiConfig.googleWebClientId
        try {
            ApiConfig.googleWebClientId = ""
            val context = ApplicationProvider.getApplicationContext<Context>()
            val authManager = GoogleAuthManager(context)
            val result = authManager.signInWithGoogle()
            assertTrue("Expected Failure result when client ID is blank", result is GoogleAuthResult.Failure)
            val failure = result as GoogleAuthResult.Failure
            assertTrue(failure.message.contains("Google Web Client ID is not configured"))
        } finally {
            ApiConfig.googleWebClientId = original
        }
    }

    @Test
    fun `sessionManager stores and clears session correctly`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val sessionManager = SessionManager(context)

        // Ensure clean initial state
        sessionManager.clearSession()
        assertFalse(sessionManager.hasSession())
        assertNull(sessionManager.getToken())
        assertNull(sessionManager.getUser())

        // Save session
        val testUser = UserProfile(
            id = "usr_neon_123",
            email = "amadiprecious899@gmail.com",
            name = "Precious Amadi"
        )
        val testToken = "test_bearer_token_xyz"
        sessionManager.saveSession(testToken, testUser)

        // Verify stored session
        assertTrue(sessionManager.hasSession())
        assertEquals(testToken, sessionManager.getToken())
        val restoredUser = sessionManager.getUser()
        assertNotNull(restoredUser)
        assertEquals("usr_neon_123", restoredUser?.id)
        assertEquals("amadiprecious899@gmail.com", restoredUser?.email)
        assertEquals("Precious Amadi", restoredUser?.name)

        // Sign out / Clear
        sessionManager.clearSession()
        assertFalse(sessionManager.hasSession())
        assertNull(sessionManager.getToken())
        assertNull(sessionManager.getUser())
    }

    @Test
    fun `authResponse resolves tokens correctly`() {
        val response1 = AuthResponse(ok = true, token = "tok_123")
        assertEquals("tok_123", response1.resolvedToken)

        val response2 = AuthResponse(ok = true, sessionToken = "session_456")
        assertEquals("session_456", response2.resolvedToken)
    }

    @Test
    fun `single reactive cart state updates immediately across all indicators`() {
        val mockRepo = MockFarmeraRepository()
        val viewModel = FarmeraViewModel(repository = mockRepo)

        // 1. Initial empty state
        assertEquals(0, viewModel.uiState.value.totalCartItemsCount)
        assertEquals(0, viewModel.uiState.value.cartItems.size)

        val product = mockRepo.getStaticProducts().first()

        // 2. Tap Add to Cart on Product Detail
        viewModel.addToCart(product, isBulk = false, quantity = 1)
        assertEquals(1, viewModel.uiState.value.totalCartItemsCount)
        assertEquals(1, viewModel.uiState.value.cartItems.first().quantity)

        // 3. Add more quantity from Product Detail
        viewModel.addToCart(product, isBulk = false, quantity = 2)
        assertEquals(3, viewModel.uiState.value.totalCartItemsCount)
        assertEquals(3, viewModel.uiState.value.cartItems.first().quantity)

        // 4. Increment item (e.g. from Basket Sheet or Cart Screen)
        viewModel.incrementCartItem(product.id)
        assertEquals(4, viewModel.uiState.value.totalCartItemsCount)

        // 5. Decrement item
        viewModel.decrementCartItem(product.id)
        assertEquals(3, viewModel.uiState.value.totalCartItemsCount)

        // 6. Decrement until removed
        viewModel.decrementCartItem(product.id)
        viewModel.decrementCartItem(product.id)
        viewModel.decrementCartItem(product.id)
        assertEquals(0, viewModel.uiState.value.totalCartItemsCount)
        assertTrue(viewModel.uiState.value.cartItems.isEmpty())
    }

    @Test
    fun `getCart returns HTTP 401 when called without valid authorization`() = runBlocking {
        val response = ApiClient.cartService.getCart("Bearer invalid_token_123")
        assertEquals(401, response.code())
        assertFalse(response.isSuccessful)
    }

    @Test
    fun `apiCart and apiCartItem json parsing supports all required backend fields`() {
        val json = """
            {
              "id": "cart_neon_987",
              "userId": "usr_precious_123",
              "updatedAt": "2026-10-05T12:00:00.000Z",
              "itemCount": 2,
              "subtotal": 15000.0,
              "items": [
                {
                  "id": "item_row_001",
                  "cartId": "cart_neon_987",
                  "productId": "ogun-plantain",
                  "quantity": 2,
                  "unitPrice": 7500.0,
                  "lineTotal": 15000.0,
                  "createdAt": "2026-10-05T11:59:00.000Z",
                  "updatedAt": "2026-10-05T12:00:00.000Z",
                  "product": {
                    "id": "ogun-plantain",
                    "slug": "ogun-plantain",
                    "name": "Ogun Plantain",
                    "price": 7500.0
                  }
                }
              ]
            }
        """.trimIndent()

        val adapter = ApiClient.moshi.adapter(ApiCart::class.java)
        val cart = adapter.fromJson(json)

        assertNotNull(cart)
        assertEquals("cart_neon_987", cart?.id)
        assertEquals("usr_precious_123", cart?.userId)
        assertEquals(2, cart?.itemCount)
        assertEquals(15000.0, cart?.subtotal)
        assertEquals(1, cart?.items?.size)

        val item = cart?.items?.first()
        assertNotNull(item)
        assertEquals("item_row_001", item?.id)
        assertEquals("cart_neon_987", item?.cartId)
        assertEquals("ogun-plantain", item?.productId)
        assertEquals(2, item?.quantity)
        assertEquals(7500.0, item?.unitPrice)
        assertEquals(15000.0, item?.lineTotal)
        assertEquals("Ogun Plantain", item?.product?.name)

        val domainCartItem = item!!.toDomainCartItem()
        assertEquals("item_row_001", domainCartItem.serverItemId)
        assertEquals("ogun-plantain", domainCartItem.product.slug)
        assertEquals(2, domainCartItem.quantity)
        assertEquals(15000, domainCartItem.lineTotal)
    }

    @Test
    fun `viewModel applyServerCart updates reactive cart state and all indicators`() {
        val mockRepo = MockFarmeraRepository()
        val viewModel = FarmeraViewModel(repository = mockRepo)

        val serverCart = ApiCart(
            id = "cart_server_cuj",
            userId = "usr_neon_buyer",
            itemCount = 3,
            subtotal = 36000.0,
            items = listOf(
                ApiCartItem(
                    id = "server_item_1",
                    productId = "benue-puna-yam",
                    quantity = 1,
                    unitPrice = 18500.0,
                    lineTotal = 18500.0,
                    product = ApiProductItem(
                        id = "benue-puna-yam",
                        slug = "benue-puna-yam",
                        name = "Benue Puna Yam",
                        price = 18500.0
                    )
                ),
                ApiCartItem(
                    id = "server_item_2",
                    productId = "ogun-plantain",
                    quantity = 2,
                    unitPrice = 7500.0,
                    lineTotal = 15000.0,
                    product = ApiProductItem(
                        id = "ogun-plantain",
                        slug = "ogun-plantain",
                        name = "Ogun Plantain",
                        price = 7500.0
                    )
                )
            )
        )

        // Apply server cart (as received from GET /api/cart or SSE cart-updated)
        viewModel.applyServerCart(serverCart)

        val state = viewModel.uiState.value
        assertEquals(2, state.cartItems.size)
        assertEquals(3, state.totalCartItemsCount)
        assertEquals(33500, state.cartItems.sumOf { it.lineTotal })
        assertEquals("server_item_1", state.cartItems[0].serverItemId)
        assertEquals("server_item_2", state.cartItems[1].serverItemId)
        assertEquals(1, viewModel.getProductQuantity("benue-puna-yam"))
        assertEquals(2, viewModel.getProductQuantity("ogun-plantain"))
        assertTrue(state.syncStatusText.contains("Server cart synced"))
    }

    @Test
    fun `live shared backend cart CRUD lifecycle succeeds with authenticated token`() = runBlocking {
        // Authenticate with backend dev-login to obtain session token
        val testEmail = "amadiprecious899@gmail.com"
        val requestBody = "{\"email\":\"$testEmail\",\"fullName\":\"Amadi Precious\"}"
            .toRequestBody("application/json".toMediaType())
        val devLoginRequest = okhttp3.Request.Builder()
            .url("${ApiConfig.BASE_URL.trimEnd('/')}/api/auth/dev-login")
            .post(requestBody)
            .build()

        val devLoginResponse = ApiClient.okHttpClient.newCall(devLoginRequest).execute()
        assertTrue("Dev login request should succeed", devLoginResponse.isSuccessful)
        val loginJson = devLoginResponse.body?.string() ?: ""
        val tokenMatch = Regex("\"token\":\"([^\"]+)\"").find(loginJson)
        assertNotNull("Session token must be returned by backend", tokenMatch)
        val sessionToken = tokenMatch!!.groupValues[1]
        val authHeader = "Bearer $sessionToken"

        // A. GET /api/cart returns HTTP 200
        val initialGet = ApiClient.cartService.getCart(authHeader)
        assertTrue("GET /api/cart should be 200 OK", initialGet.isSuccessful)
        assertNotNull(initialGet.body()?.cart)

        // B. Clear cart first to establish clean baseline
        ApiClient.cartService.clearCart(authHeader)

        // C. POST /api/cart/items adds a produce product
        val addResponse = ApiClient.cartService.addItem(
            authHeader,
            AddCartItemRequest(productId = "kaduna-maize", quantity = 2)
        )
        assertTrue("POST /api/cart/items should succeed", addResponse.isSuccessful)
        val cartAfterAdd = addResponse.body()?.cart
        assertNotNull(cartAfterAdd)
        val addedItem = cartAfterAdd?.items?.find { it.productId == "kaduna-maize" }
        assertNotNull("Added item should be in cart", addedItem)
        assertEquals(2, addedItem?.quantity)
        val serverItemId = addedItem!!.id

        // D. PATCH /api/cart/items/:itemId updates quantity
        val patchResponse = ApiClient.cartService.updateItem(
            authHeader,
            serverItemId,
            UpdateCartItemRequest(quantity = 4)
        )
        assertTrue("PATCH /api/cart/items/:id should succeed", patchResponse.isSuccessful)
        val cartAfterPatch = patchResponse.body()?.cart
        val patchedItem = cartAfterPatch?.items?.find { it.id == serverItemId }
        assertEquals(4, patchedItem?.quantity)

        // E. DELETE /api/cart/items/:itemId removes product
        val deleteItemResponse = ApiClient.cartService.removeItem(authHeader, serverItemId)
        assertTrue("DELETE /api/cart/items/:id should succeed", deleteItemResponse.isSuccessful)
        val cartAfterDelete = deleteItemResponse.body()?.cart
        assertFalse("Item should no longer be in cart", cartAfterDelete?.items?.any { it.id == serverItemId } == true)

        // F. DELETE /api/cart clears cart completely
        val clearResponse = ApiClient.cartService.clearCart(authHeader)
        assertTrue("DELETE /api/cart should succeed", clearResponse.isSuccessful)
        assertEquals(0, clearResponse.body()?.cart?.itemCount)
        assertTrue(clearResponse.body()?.cart?.items.isNullOrEmpty())
    }
}
