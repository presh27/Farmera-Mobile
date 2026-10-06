package com.example.data

import com.example.model.BannerSlide
import com.example.model.Category
import com.example.model.Product
import com.example.network.ApiClient
import com.example.network.ProductApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NetworkFarmeraRepository(
    private val apiService: ProductApiService = ApiClient.productService,
    private val fallbackRepository: FarmeraRepository = MockFarmeraRepository()
) : FarmeraRepository {

    // In-memory cache for fast navigation without redundant fetches
    private var cachedProducts: List<Product>? = null
    private var cachedCategories: List<Category>? = null

    private val defaultCategories = listOf(
        Category("cat_tubers", "Tubers & Roots", "tubers", "yam", 0xFFFFF1E6),
        Category("cat_fruits", "Fruits", "fruits", "orange", 0xFFFFFBEB),
        Category("cat_vegetables", "Vegetables", "vegetables", "pepper", 0xFFECFDF5),
        Category("cat_grains", "Grains & Beans", "grains", "beans", 0xFFFEF3C7),
        Category("cat_oils", "Oils", "oils", "palmoil", 0xFFFFF7ED),
        Category("cat_pantry", "Pantry Staples", "pantry", "garri", 0xFFFDF4FF)
    )

    private val heroSlides = listOf(
        BannerSlide(
            id = "slide_1",
            headline = "Fresh food. Fewer layers.",
            subtitle = "Shop produce sourced from farms and verified producers across Nigeria.",
            ctaText = "Shop Now",
            promoTag = "DIRECT SOURCING"
        ),
        BannerSlide(
            id = "slide_2",
            headline = "Buy more. Pay better.",
            subtitle = "Bulk pricing for households, restaurants, caterers and food businesses.",
            ctaText = "Explore Bulk",
            promoTag = "WHOLESALE SAVINGS"
        ),
        BannerSlide(
            id = "slide_3",
            headline = "Harvesting this week",
            subtitle = "Discover fresh tubers, peppers and citrus picked within the last 48 hours.",
            ctaText = "See Harvest",
            promoTag = "FRESH HARVEST"
        )
    )

    override fun getProducts(): Flow<List<Product>> = flow {
        // Return in-memory cached products if available
        cachedProducts?.let {
            if (it.isNotEmpty()) {
                emit(it)
            }
        }

        try {
            val response = apiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val apiResponse = response.body()!!
                val apiItems = apiResponse.products ?: emptyList()
                if (apiItems.isNotEmpty()) {
                    val domainProducts = apiItems.map { it.toDomainProduct() }
                    cachedProducts = domainProducts
                    cachedCategories = deriveCategories(domainProducts)
                    emit(domainProducts)
                    return@flow
                }
            }
        } catch (e: Exception) {
            // Network error handled gracefully without crashing
        }

        // If network failed and no cache, fall back to mock repository
        if (cachedProducts.isNullOrEmpty()) {
            val fallback = (fallbackRepository as? MockFarmeraRepository)?.let {
                val staticProds = it.getStaticProducts()
                cachedProducts = staticProds
                cachedCategories = deriveCategories(staticProds)
                staticProds
            } ?: emptyList()
            emit(fallback)
        }
    }

    override fun getProductById(id: String): Flow<Product?> = flow {
        val cached = cachedProducts?.find { it.id == id || it.slug == id }
        if (cached != null) {
            emit(cached)
            return@flow
        }

        try {
            val response = apiService.getProductBySlug(id)
            if (response.isSuccessful && response.body()?.product != null) {
                val domainProduct = response.body()!!.product!!.toDomainProduct()
                cachedProducts = (cachedProducts ?: emptyList()).filterNot { it.id == domainProduct.id } + domainProduct
                emit(domainProduct)
                return@flow
            }
        } catch (e: Exception) {
            // Network failure handled gracefully
        }

        // Fallback check
        emit(cachedProducts?.find { it.id == id || it.slug == id })
    }

    override fun getCategories(): Flow<List<Category>> = flow {
        val products = cachedProducts
        if (products != null && products.isNotEmpty()) {
            val derived = deriveCategories(products)
            cachedCategories = derived
            emit(derived)
            return@flow
        }

        cachedCategories?.let {
            if (it.isNotEmpty()) {
                emit(it)
                return@flow
            }
        }

        emit(defaultCategories)
    }

    override fun getHeroSlides(): Flow<List<BannerSlide>> = flow {
        emit(heroSlides)
    }

    override fun searchProducts(query: String): Flow<List<Product>> = flow {
        val products = cachedProducts ?: emptyList()
        if (query.isBlank()) {
            emit(products)
        } else {
            val q = query.trim().lowercase()
            val filtered = products.filter {
                it.name.lowercase().contains(q) ||
                it.category.lowercase().contains(q) ||
                it.sourceLocation.lowercase().contains(q) ||
                it.farmName.lowercase().contains(q) ||
                it.shortDescription.lowercase().contains(q)
            }
            emit(filtered)
        }
    }

    override fun getFreshFromFarms(): Flow<List<Product>> = flow {
        val products = cachedProducts ?: emptyList()
        emit(products.take(6))
    }

    override fun getHarvestingThisWeek(): Flow<List<Product>> = flow {
        val products = cachedProducts ?: emptyList()
        val harvesting = products.filter { it.isHarvestingThisWeek }
        emit(if (harvesting.isNotEmpty()) harvesting else products.take(4))
    }

    override fun getBulkBuys(): Flow<List<Product>> = flow {
        val products = cachedProducts ?: emptyList()
        val bulk = products.filter { it.isBulkAvailable }
        emit(if (bulk.isNotEmpty()) bulk else products.take(4))
    }

    fun invalidateCache() {
        cachedProducts = null
        cachedCategories = null
    }

    private fun deriveCategories(products: List<Product>): List<Category> {
        if (products.isEmpty()) return defaultCategories

        val distinctCategories = products.map { it.category to it.categorySlug }.distinctBy { it.second }
        return distinctCategories.map { (label, slug) ->
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
                name = label,
                slug = slug,
                itemKey = itemKey,
                bgTintHex = tint
            )
        }
    }
}
