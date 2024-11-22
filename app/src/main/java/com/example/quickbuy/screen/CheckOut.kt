package com.example.quickbuy.screen

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.quickbuy.R
import com.example.quickbuy.navigation.Screen

@Composable
fun CheckOut(navController: NavHostController) {
    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        CheckOutHeader(modifier = Modifier.align(Alignment.TopStart))
        CheckOutDetails(modifier = Modifier.align(Alignment.Center))
        Column(
            modifier = Modifier
                .padding(horizontal = 18.dp)
                .align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = "Choose payment option",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Cash", color = if (isSystemInDarkTheme()) Color.White else Color.Black)
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Blue.copy(0.5f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Add new payment method",
                    fontWeight = FontWeight.SemiBold,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
                Icon(
                    painter = painterResource(id = R.drawable.add_circle),
                    contentDescription = null,
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Blue
                )
            }
            Spacer(modifier = Modifier.height(36.dp))
            Button(
                onClick = {
                          navController.navigate(Screen.Success)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 9.dp, vertical = 27.dp),
                shape = RoundedCornerShape(9.dp)
            ) {
                Text(text = "Confirm order", color = Color.White)
            }
        }
    }
}


@Composable
fun CheckOutHeader(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 36.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_backward),
            contentDescription = null,
            modifier = Modifier
                .size(27.dp)
                .align(Alignment.TopStart)
                .clickable {

                },
            if (isSystemInDarkTheme()) Color.White else Color.Black
        )

        Text(
            text = "CheckOut",
            style = MaterialTheme.typography.titleLarge,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
            modifier = Modifier.align(Alignment.Center)
        )
        Icon(
            painter = painterResource(id = R.drawable.more_vertical),
            contentDescription = null,
            modifier = Modifier
                .size(27.dp)
                .align(Alignment.TopEnd),
            if (isSystemInDarkTheme()) Color.White else Color.Black
        )
    }
}

@Composable
fun CheckOutDetails(
    modifier: Modifier = Modifier,
    itemCount: Int = 4,
    subTotal: Double = 423.0,
    discount: Double = 2.0,
    deliveryCharges: Double = 20.0,

    ) {
    val total = subTotal - discount + deliveryCharges
    val details = listOf(
        "Items" to itemCount.toString(),
        "SubTotal" to "$$subTotal",
        "Discount" to "-$$discount",
        "Delivery Charges" to "$$deliveryCharges"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(18.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "Order Summary", style = MaterialTheme.typography.titleMedium)

            details.forEach { (label, value) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = label)
                    Text(text = value)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(thickness = 2.dp, color = Color.Black)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Total", fontWeight = FontWeight.SemiBold)
                Text(text = "$$total", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}
