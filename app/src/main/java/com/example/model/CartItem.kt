package com.example.model

data class CartItem(
    val product: Product,
    val quantity: Int,
    val isBulk: Boolean = false,
    val serverItemId: String? = null
) {
    val unitPrice: Int
        get() = if (isBulk && product.bulkPrice != null) product.bulkPrice else product.price

    val lineTotal: Int
        get() = unitPrice * quantity

    val formattedLineTotal: String
        get() = "₦${"%,d".format(lineTotal)}"
}

data class BannerSlide(
    val id: String,
    val headline: String,
    val subtitle: String,
    val ctaText: String,
    val promoTag: String? = null
)
