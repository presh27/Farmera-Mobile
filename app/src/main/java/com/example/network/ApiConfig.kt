package com.example.network

object ApiConfig {
    // Single configurable location for the existing Farmera backend base URL
    const val BASE_URL = "https://aperture-co-646783647451.europe-west2.run.app"

    // Auth Endpoints
    const val AUTH_GOOGLE = "api/auth/google"
    const val AUTH_ME = "api/auth/me"
    const val AUTH_LOGOUT = "api/auth/logout"

    // Product Endpoints
    const val PRODUCTS = "api/products"
    const val PRODUCT_DETAIL = "api/products/{slug}"

    // Cart Endpoints
    const val CART = "api/cart"
    const val CART_ITEMS = "api/cart/items"
    const val CART_ITEM_DETAIL = "api/cart/items/{itemId}"
    const val CART_EVENTS = "api/cart/events"

    // Google OAuth 2.0 Web Application Client ID (public OAuth identifier)
    var googleWebClientId: String = "668764933589-mmede6d874tpcfil4nu1t8rjt4209le9.apps.googleusercontent.com"
}
