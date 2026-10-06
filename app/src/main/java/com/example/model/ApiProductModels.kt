package com.example.model

import com.example.network.ApiConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiProductListResponse(
    @Json(name = "ok") val ok: Boolean? = true,
    @Json(name = "count") val count: Int? = 0,
    @Json(name = "products") val products: List<ApiProductItem>? = emptyList(),
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiProductDetailResponse(
    @Json(name = "ok") val ok: Boolean? = true,
    @Json(name = "product") val product: ApiProductItem? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiProductSpec(
    @Json(name = "label") val label: String? = null,
    @Json(name = "value") val value: String? = null,
    @Json(name = "iconName") val iconName: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiProductItem(
    @Json(name = "id") val id: String,
    @Json(name = "dbId") val dbId: String? = null,
    @Json(name = "slug") val slug: String,
    @Json(name = "name") val name: String,
    @Json(name = "subtitle") val subtitle: String? = null,
    @Json(name = "edition") val edition: String? = null,
    @Json(name = "category") val category: String? = null,
    @Json(name = "categoryLabel") val categoryLabel: String? = null,
    @Json(name = "subCategory") val subCategory: String? = null,
    @Json(name = "price") val price: Double? = 0.0,
    @Json(name = "rating") val rating: Double? = 5.0,
    @Json(name = "reviewCount") val reviewCount: Int? = 0,
    @Json(name = "tag") val tag: String? = null,
    @Json(name = "stockQuantity") val stockQuantity: Int? = 0,
    @Json(name = "inStock") val inStock: Boolean? = true,
    @Json(name = "description") val description: String? = null,
    @Json(name = "imageUrl") val imageUrl: String? = null,
    @Json(name = "image_url") val imageUrlSnake: String? = null,
    @Json(name = "images") val images: List<String>? = emptyList(),
    @Json(name = "specs") val specs: List<ApiProductSpec>? = emptyList(),
    @Json(name = "boxContents") val boxContents: List<String>? = emptyList(),
    @Json(name = "careNotes") val careNotes: List<String>? = emptyList(),
    @Json(name = "longDescription") val longDescription: List<String>? = emptyList()
) {
    fun toDomainProduct(): Product {
        val resolvedPrice = (price ?: 0.0).toInt()
        val rawCat = categoryLabel?.takeIf { it.isNotBlank() }
            ?: category?.takeIf { it.isNotBlank() }
            ?: "Produce"

        val resolvedCategory = rawCat
        val resolvedCategorySlug = when (rawCat.trim().lowercase()) {
            "tubers & roots", "tubers and roots", "tubers" -> "tubers"
            "fruits", "fruit" -> "fruits"
            "vegetables", "veggies", "vegetable" -> "vegetables"
            "grains & beans", "grains and beans", "grains", "beans" -> "grains"
            "oils", "oil" -> "oils"
            "pantry staples", "pantry" -> "pantry"
            else -> rawCat.lowercase().replace("&", "and").replace("[^a-z0-9]+".toRegex(), "-").trim('-')
        }

        val resolvedUnit = subCategory?.takeIf { it.isNotBlank() }
            ?: specs?.firstOrNull { it.label.equals("UNIT", ignoreCase = true) }?.value
            ?: "1 unit"

        val resolvedLocation = specs?.firstOrNull { it.label.equals("ORIGIN", ignoreCase = true) }?.value
            ?: edition?.takeIf { it.isNotBlank() }
            ?: "Direct Farm Source"

        val resolvedFarm = edition?.takeIf { it.isNotBlank() } ?: "Verified Farm Partner"
        val hasStock = inStock ?: ((stockQuantity ?: 0) > 0)
        val bulkPriceCalc = if (resolvedPrice > 50) (resolvedPrice * 0.9).toInt() else null

        val rawImg = imageUrl?.takeIf { it.isNotBlank() }
            ?: imageUrlSnake?.takeIf { it.isNotBlank() }
            ?: images?.firstOrNull { it.isNotBlank() }

        val resolvedImageModel = ProductImageResolver.resolveImage(slug)
        val fullImageUrl = when (resolvedImageModel) {
            is String -> resolvedImageModel
            is Int -> "android.resource://com.example/$resolvedImageModel"
            else -> rawImg?.let { img ->
                if (img.startsWith("http://") || img.startsWith("https://")) img
                else "${ApiConfig.BASE_URL.trimEnd('/')}/${img.trimStart('/')}"
            }
        }

        val specSummary = specs?.joinToString(" · ") { "${it.label}: ${it.value}" } ?: ""
        val fullHarvestInfo = listOfNotNull(subtitle?.takeIf { it.isNotBlank() }, specSummary.takeIf { it.isNotBlank() })
            .joinToString("\n")
            .ifBlank { "Sourced direct from verified farm partners across Nigeria." }

        val storage = careNotes?.joinToString("\n")?.ifBlank { "Store in a cool dry place." }
            ?: "Store in a cool dry place."

        // Produce-specific icon key mapping
        val lowerSlug = slug.lowercase()
        val lowerCat = resolvedCategorySlug.lowercase()
        val iconKey = when {
            lowerSlug.contains("yam") -> "yam"
            lowerSlug.contains("plantain") -> "plantain"
            lowerSlug.contains("tomato") -> "tomatoes"
            lowerSlug.contains("onion") -> "onions"
            lowerSlug.contains("pepper") || lowerSlug.contains("atarodo") -> "pepper"
            lowerSlug.contains("palm") || lowerSlug.contains("oil") -> "palmoil"
            lowerSlug.contains("garri") -> "garri"
            lowerSlug.contains("bean") -> "beans"
            lowerSlug.contains("maize") || lowerSlug.contains("corn") -> "maize"
            lowerSlug.contains("orange") || lowerSlug.contains("citrus") -> "orange"
            lowerSlug.contains("avocado") || lowerSlug.contains("pear") -> "avocado"
            lowerCat.contains("tuber") -> "yam"
            lowerCat.contains("fruit") -> "orange"
            lowerCat.contains("vegetable") -> "pepper"
            lowerCat.contains("grain") -> "beans"
            lowerCat.contains("oil") -> "palmoil"
            lowerCat.contains("pantry") -> "garri"
            else -> "crate"
        }

        return Product(
            id = id,
            slug = slug,
            name = name,
            category = resolvedCategory,
            categorySlug = resolvedCategorySlug,
            sourceLocation = resolvedLocation,
            farmName = resolvedFarm,
            isVerifiedPartner = true,
            unit = resolvedUnit,
            price = resolvedPrice,
            bulkPrice = bulkPriceCalc,
            rating = rating ?: 4.9,
            reviewsCount = reviewCount ?: 42,
            availability = if (hasStock) "Available now (${stockQuantity ?: 1} in stock)" else "Pre-order / Backorder",
            isHarvestingThisWeek = hasStock,
            isBulkAvailable = (stockQuantity ?: 0) >= 4,
            shortDescription = description ?: subtitle ?: name,
            harvestInfo = fullHarvestInfo,
            storageGuidance = storage,
            badgeText = tag,
            iconKey = iconKey,
            imageUrl = fullImageUrl
        )
    }
}
