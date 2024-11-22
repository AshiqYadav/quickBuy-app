package com.example.quickbuy

import BottomNavBar
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.quickbuy.navigation.MainNavGraph
import com.example.quickbuy.navigation.Screen
import com.example.quickbuy.ui.theme.QuickBuyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuickBuyTheme {
                App()
            }
        }
    }
}
@Composable
fun App() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStackEntry.value?.destination?.route
    val noBottomBarRoutes = listOf(Screen.details,Screen.showAllProduct,Screen.Success,Screen.CheckOut)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (currentDestination !in noBottomBarRoutes){
                BottomNavBar(navController)
            }
        }
    ) { paddingValues ->
        MainNavGraph(navController)
        Modifier.padding(paddingValues)
    }
}