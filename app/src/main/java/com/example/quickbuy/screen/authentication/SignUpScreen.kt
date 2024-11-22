package com.example.quickbuy.screen.authentication

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.quickbuy.MainActivity
import com.example.quickbuy.R
import com.example.quickbuy.navigation.Screen
import com.example.quickbuy.screen.components.GetCircularProgressLoadingIndicator
import com.example.quickbuy.ui.theme.Violet
import com.example.quickbuy.ui.theme.blue
import com.example.quickbuy.viewmodel.AuthState
import com.example.quickbuy.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
) {

    val authState = authViewModel.uiState.observeAsState()
    val context = LocalContext.current
    when (val state = authState.value) {
        is AuthState.Authenticated -> {
            Toast.makeText(context, "Sign Up Success", Toast.LENGTH_SHORT).show()
            navController.context.startActivity(
                Intent(
                    navController.context,
                    MainActivity::class.java
                )
            )
            (navController.context as Activity).finish()
        }
        is AuthState.Unauthenticated -> {
            SignUpScreenContent(navController = navController, authViewModel = authViewModel)
        }
        is AuthState.Loading -> {
            GetCircularProgressLoadingIndicator()
        }
        is AuthState.Error -> {
            Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            SignUpScreenContent(navController = navController, authViewModel = authViewModel)
        }
        null -> Unit
    }
}

@Composable
fun SignUpScreenContent(navController: NavHostController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .pointerInput(Unit){
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 16.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Sign up",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 32.dp)
            ) {
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text(text = "Username") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(text = "Email") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(text = "Password") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 1
                )
                TextButton(
                    onClick = {
                        navController.navigate(Screen.signIn)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(
                        text = "Already have an account?",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                    androidx.compose.material.Icon(
                        painter = painterResource(id = R.drawable.arrow_forward),
                        contentDescription = null,
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
                Button(
                    onClick = { authViewModel.signUp(email, password, userName) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = if (isSystemInDarkTheme()) ButtonDefaults.buttonColors(Violet) else ButtonDefaults.buttonColors(
                        blue
                    )
                ) {
                    Text(text = "Sign In", color = Color.White)
                }
            }
        }
    }
}
