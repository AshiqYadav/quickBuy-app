package com.example.quickbuy.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quickbuy.screen.Cart
import com.example.quickbuy.screen.CheckOut
import com.example.quickbuy.screen.Details
import com.example.quickbuy.screen.Home
import com.example.quickbuy.screen.Profile
import com.example.quickbuy.screen.ShowAllProducts
import com.example.quickbuy.screen.Success
import com.example.quickbuy.screen.Wishlist

@Composable
fun MainNavGraph(navController: NavHostController){
    NavHost(navController = navController, startDestination = Screen.home) {
        composable(Screen.home){
            Home(navController)
        }

        composable(Screen.wishlist) {
            Wishlist(navController)
        }

        composable(Screen.cart) {
            Cart(navController)
        }

        composable(Screen.profile) {
            Profile(navController)
        }

        composable(Screen.details){
            val productId = it.arguments?.getString("productId") ?: ""
            Details(navController,productId = productId)
        }

        composable(Screen.showAllProduct){
            val title = it.arguments?.getString("title") ?: ""
            ShowAllProducts(navController = navController, title = title)
        }

        composable(Screen.Success) {
            Success(navController)
        }
        composable(Screen.CheckOut){
            CheckOut(navController)
        }

    }
}