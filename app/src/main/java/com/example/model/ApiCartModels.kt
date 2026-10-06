package com.example.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApiCartResponse(
    @Json(name = "ok") val ok: Boolean? = true,
    @Json(name = "cart") val cart: ApiCart? = null,
    @Json(name = "error") val error: String? = null
)

@JsonClass(generateAdapter = true)
data class ApiCart(
    @Json(name = "id") val id: String,
    @Json(name = "userId") val userId: String? = null,
    @Json(name = "updatedAt") val updatedAt: String? = null,
    @Json(name = "itemCount") val itemCount: Int? = 0,
    @Json(name = "subtotal") val subtotal: Double? = 0.0,
    @Json(name = "items") val items: List<ApiCartItem>? = emptyList()
)

@JsonClass(generateAdapter = true)
data class ApiCartItem(
    @Json(name = "id") val id: String,
    @Json(name = "cartId") val cartId: String? = null,
    @Json(name = "productId") val productId: String,
    @Json(name = "quantity") val quantity: Int = 1,
    @Json(name = "unitPrice") val unitPrice: Double? = 0.0,
    @Json(name = "lineTotal") val lineTotal: Double? = 0.0,
    @Json(name = "createdAt") val createdAt: String? = null,
    @Json(name = "updatedAt") val updatedAt: String? = null,
    @Json(name = "product") val product: ApiProductItem? = null
) {
    fun toDomainCartItem(knownProducts: List<Product> = emptyList()): CartItem {
        val matchedProduct = knownProducts.find {
            it.id.equals(productId, ignoreCase = true) ||
            it.slug.equals(productId, ignoreCase = true) ||
            (product != null && (it.id.equals(product.id, ignoreCase = true) || it.slug.equals(product.slug, ignoreCase = true)))
        }

        val baseProduct = matchedProduct ?: product?.toDomainProduct() ?: Product(
            id = productId,
            slug = productId,
            name = productId.replace("-", " ").replaceFirstChar { it.uppercase() },
            category = "Produce",
            sourceLocation = "Direct Farm Source",
            farmName = "Verified Farm Partner",
            unit = "1 unit",
            price = (unitPrice ?: 0.0).toInt(),
            shortDescription = "Fresh farm produce.",
            harvestInfo = "Harvested from verified partner farms.",
            storageGuidance = "Store in a cool dry place.",
            categorySlug = "produce",
            iconKey = "crate"
        )

        val domainProduct = if (unitPrice != null && unitPrice > 0) {
            baseProduct.copy(price = unitPrice.toInt())
        } else {
            baseProduct
        }

        return CartItem(
            product = domainProduct,
            quantity = quantity,
            isBulk = false,
            serverItemId = id
        )
    }
}

@JsonClass(generateAdapter = true)
data class AddCartItemRequest(
    @Json(name = "productId") val productId: String,
    @Json(name = "quantity") val quantity: Int = 1
)

@JsonClass(generateAdapter = true)
data class UpdateCartItemRequest(
    @Json(name = "quantity") val quantity: Int
)
