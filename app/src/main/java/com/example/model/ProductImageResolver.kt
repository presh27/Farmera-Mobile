package com.example.model

import com.example.R

/**
 * Deterministic product-image resolver keyed strictly by product slug.
 * Prevents arbitrary or mismatched remote URLs from dictating product imagery.
 * Maps every single Farmera agricultural product to its true authentic produce photography.
 */
object ProductImageResolver {

    /**
     * Resolves the exact image model (drawable resource or verified URL) for a product slug.
     * Fully compatible with Coil's AsyncImage and SubcomposeAsyncImage.
     */
    fun resolveImage(slug: String): Any {
        return when (slug.trim().lowercase()) {
            // Corrected Nigerian produce with authentic local photographs
            "benue-puna-yam" -> R.drawable.prod_benue_yam
            "niger-brown-beans" -> R.drawable.prod_brown_beans
            "abia-red-palm-oil" -> R.drawable.prod_red_palm_oil
            "edo-yellow-garri" -> R.drawable.prod_yellow_garri
            "enugu-white-garri" -> R.drawable.prod_white_garri
            "ogun-plantain" -> R.drawable.prod_ogun_plantain

            // Preserved existing verified produce photos
            "kano-tomatoes" -> "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?auto=format&fit=crop&w=800&q=80"
            "kano-red-onions" -> "https://images.unsplash.com/photo-1618512496248-a07fe83aa8cb?auto=format&fit=crop&w=800&q=80"
            "fresh-pepper" -> "https://images.unsplash.com/photo-1588252303782-cb80119abd6d?auto=format&fit=crop&w=800&q=80"
            "kaduna-maize" -> "https://images.unsplash.com/photo-1551754655-cd27e38d2076?auto=format&fit=crop&w=800&q=80"
            "sweet-oranges" -> "https://images.unsplash.com/photo-1611080626919-7cf5a9dbab5b?auto=format&fit=crop&w=800&q=80"
            "avocado-pear" -> "https://images.unsplash.com/photo-1523049673857-eb18f1d7b578?auto=format&fit=crop&w=800&q=80"

            else -> R.drawable.img_hero_basket
        }
    }

    /**
     * Returns true if the product has a dedicated high-fidelity image mapping.
     */
    fun hasDedicatedImage(slug: String): Boolean {
        return when (slug.trim().lowercase()) {
            "benue-puna-yam",
            "niger-brown-beans",
            "abia-red-palm-oil",
            "edo-yellow-garri",
            "enugu-white-garri",
            "ogun-plantain",
            "kano-tomatoes",
            "kano-red-onions",
            "fresh-pepper",
            "kaduna-maize",
            "sweet-oranges",
            "avocado-pear" -> true
            else -> false
        }
    }
}
