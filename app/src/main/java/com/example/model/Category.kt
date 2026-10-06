package com.example.model

data class Category(
    val id: String,
    val name: String,
    val slug: String,
    val itemKey: String,
    val bgTintHex: Long = 0xFFF3F4F6
)
