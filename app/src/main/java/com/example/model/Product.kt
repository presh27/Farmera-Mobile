package com.example.model

data class Product(
    val id: String,
    val slug: String,
    val name: String,
    val category: String,
    val sourceLocation: String,
    val farmName: String,
    val isVerifiedPartner: Boolean = true,
    val unit: String,
    val price: Int, // Price in Nigerian Naira (₦)
    val bulkPrice: Int? = null,
    val rating: Double = 4.8,
    val reviewsCount: Int = 142,
    val availability: String = "Available — harvested this week",
    val isHarvestingThisWeek: Boolean = false,
    val isBulkAvailable: Boolean = false,
    val shortDescription: String,
    val harvestInfo: String,
    val storageGuidance: String,
    val deliveryInfo: String = "Next-day delivery across Lagos (Island & Mainland). Sourced fresh from farm.",
    val categorySlug: String,
    val badgeText: String? = null,
    val iconKey: String = "yam",
    val imageUrl: String? = null
) {
    val formattedPrice: String
        get() = "₦${"%,d".format(price)}"

    val formattedBulkPrice: String?
        get() = bulkPrice?.let { "₦${"%,d".format(it)}" }

    val imageModel: Any
        get() = ProductImageResolver.resolveImage(slug)
}
