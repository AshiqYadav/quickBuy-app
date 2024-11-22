package com.example.quickbuy.screen.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.quickbuy.MainActivity
import com.example.quickbuy.navigation.AuthenticationNavGraph
import com.example.quickbuy.screen.authentication.ui.theme.QuickBuyTheme
import com.example.quickbuy.screen.components.GetCircularProgressLoadingIndicator
import com.example.quickbuy.viewmodel.AuthViewModel
import com.example.quickbuy.viewmodel.AuthState
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        installSplashScreen()
        setContent {
            QuickBuyTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {innerPadding->
                    val navController: NavHostController = rememberNavController()
                    val authState by authViewModel.uiState.observeAsState()
                    when (authState) {
                        is AuthState.Authenticated -> {
                            LaunchedEffect(Unit) {
                                startActivity(Intent(this@AuthenticationActivity, MainActivity::class.java))
                                finish()
                            }
                        }
                        is AuthState.Unauthenticated -> {
                            AuthenticationNavGraph(navHostController = navController,modifier = Modifier.padding(innerPadding))
                        }
                        is AuthState.Error -> {
                            Toast.makeText(this, "error -> ${(authState as AuthState.Error).message}", Toast.LENGTH_SHORT).show()
                        }
                        is AuthState.Loading -> {
                            GetCircularProgressLoadingIndicator()
                        }
                        null -> Unit
                    }
                }
            }
        }
    }
}
