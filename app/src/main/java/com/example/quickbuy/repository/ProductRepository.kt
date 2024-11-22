package com.example.quickbuy.repository

import com.example.quickbuy.model.CartItem
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.model.UserProfile

interface ProductRepository{
    suspend fun addUser(userProfile: UserProfile,userId: String)
    suspend fun getUserData(userId: String):UserProfile
    suspend fun getCategories():List<String>
    suspend fun getAllProduct():List<ProductsItem>
    suspend fun getProductById(productId: String) : ProductsItem


    suspend fun getAllLikedProduct(userId: String):List<ProductsItem>
    suspend fun addLikedProduct(userId:String,productId : String)
    suspend fun removeLikedProduct(userId:String,productId : String)
    suspend fun isLiked(userId: String,productId: String):Boolean

    suspend fun getAllCartProduct(userId: String):List<ProductsItem>
    suspend fun isProductInCart(userId: String,productId: String):Boolean
    suspend fun addToCart(userId:String,productId : String)
    suspend fun removeFromCart(userId:String,productId : String)
    suspend fun getCartProductQuantity(userId: String):List<CartItem>
    suspend fun incrementProductQuantity(userId: String,updatedCartItem: CartItem)
    suspend fun decrementProductQuantity(userId: String,updatedCartItem: CartItem)
}