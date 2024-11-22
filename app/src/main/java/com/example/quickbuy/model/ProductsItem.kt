package com.example.quickbuy.model

data class ProductsItem(
    val category: String = "",
    val description: String = "",
    val id: String = "",
    val image: String = "",
    val price: Double = 0.0,
    val rating: Rating = Rating(),
    val title: String = ""
)