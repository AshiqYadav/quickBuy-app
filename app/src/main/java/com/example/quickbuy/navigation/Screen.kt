package com.example.quickbuy.navigation

object Screen {
    const val home = "home"
    const val profile = "profile"
    const val cart = "cart"
    const val wishlist = "wishlist"

    const val showAllProduct = "showAllProducts/{title}"
    const val details = "details/{productId}"
    const val Success = "Succees"
    const val CheckOut = "Checkout"

    const val signIn = "signIn"
    const val signUp = "signUp"


    fun detailsWithId(productId: String) = "details/$productId"
    fun showAllProductWithTitle(title : String) = "showAllProducts/$title"
}