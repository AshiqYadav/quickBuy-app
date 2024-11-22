package com.example.quickbuy.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.quickbuy.R
import com.example.quickbuy.navigation.Screen

@Composable
fun Success(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.success_img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center)
        )

        Box(modifier = Modifier.align(Alignment.CenterEnd)) {
            Column() {
                Text(
                    text = "Success !",
                    modifier = Modifier.padding(top = 200.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 40.sp,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
                Text(
                    text = "Your will be delivered soon.\nThank you choosing our app!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Box(
            modifier = Modifier
                .padding(horizontal = 27.dp, vertical = 27.dp)
                .align(Alignment.BottomEnd)
        ) {
            Button(
                onClick = { navController.navigate(Screen.home) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text(text = "CONTINUE SHOPPING", color = Color.White)
            }
        }
    }
}

