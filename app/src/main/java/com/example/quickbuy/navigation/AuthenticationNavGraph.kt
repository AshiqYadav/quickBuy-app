package com.example.quickbuy.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.quickbuy.screen.authentication.SignInScreen
import com.example.quickbuy.screen.authentication.SignUpScreen

@Composable
fun AuthenticationNavGraph(navHostController: NavHostController, modifier: Modifier = Modifier){
    NavHost(navController = navHostController, startDestination = Screen.signIn){
        composable(Screen.signIn){
            SignInScreen(navController = navHostController)
        }
        composable(Screen.signUp){
            SignUpScreen(navController = navHostController)
        }
    }
}