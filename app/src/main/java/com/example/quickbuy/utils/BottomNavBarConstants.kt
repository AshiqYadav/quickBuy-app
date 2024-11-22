package com.example.quickbuy.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import com.example.quickbuy.model.BottomNavItem

object BottomNavBarConstants {
    val BottomNavItem = listOf(
        BottomNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            route = "home"
        ),
        BottomNavItem(
            label = "Wishlist",
            icon = Icons.Default.Favorite,
            route = "wishlist"
        ),
        BottomNavItem(
            label = "Cart",
            icon = Icons.Default.ShoppingCart,
            route = "cart"
        ),
        BottomNavItem(
            label = "Profile",
            icon = Icons.Default.Person,
            route = "profile"
        ),

    )
}