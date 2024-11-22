package com.example.quickbuy.screen

import android.widget.ImageView
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.Glide
import com.example.quickbuy.R
import com.example.quickbuy.model.CartItem
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.navigation.Screen
import com.example.quickbuy.screen.components.GetCircularProgressLoadingIndicator
import com.example.quickbuy.ui.theme.Cinereous
import com.example.quickbuy.ui.theme.DarkGrey
import com.example.quickbuy.ui.theme.Manatee
import com.example.quickbuy.ui.theme.Violet
import com.example.quickbuy.viewmodel.CartViewModel
import com.example.quickbuy.viewmodel.Result

@Composable
fun Cart(navController: NavHostController, cartViewModel: CartViewModel = hiltViewModel()) {
    val uiState = cartViewModel.uiState.observeAsState()
    val mergedProductList by cartViewModel.mergedProductList.collectAsState()
    LaunchedEffect(false) {
        cartViewModel.refreshCart()
    }
    when (val state = uiState.value) {
        is Result.Loading -> GetCircularProgressLoadingIndicator()
        is Result.Error -> Toast.makeText(
            LocalContext.current,
            "error ${state.message}",
            Toast.LENGTH_SHORT
        ).show()

        is Result.Success -> if (mergedProductList.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your cart is empty.",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            CartScreenContent(
                cartViewModel = cartViewModel,
                navController = navController,
                mergedProductList = mergedProductList
            )
        }

        else -> Unit
    }
}

@Composable
fun CartScreenContent(
    cartViewModel: CartViewModel,
    navController: NavHostController,
    mergedProductList: List<Pair<ProductsItem, CartItem>>,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 136.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(36.dp))
                CartHeader()
            }
            items(mergedProductList) { (productItem, cartItem) ->
                ProductCardView(
                    cartViewModel = cartViewModel,
                    productItem = productItem,
                    navController = navController,
                    cartItem = cartItem
                )
            }
        }
        PlaceOrderButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 96.dp)
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme()) DarkGrey else Manatee),
            cartViewModel = cartViewModel,
            navController = navController
        )
    }
}

@Composable
fun CartHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
    ) {
        Text(
            text = "Cart",
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
fun ProductCardView(
    cartViewModel: CartViewModel,
    productItem: ProductsItem,
    navController: NavHostController,
    cartItem: CartItem,
) {
    val showToast by cartViewModel.showToast.collectAsState()
    if (showToast) {
        Toast.makeText(
            LocalContext.current,
            "Product successfully removed from cart",
            Toast.LENGTH_SHORT
        ).show()
        cartViewModel.showToastSuccess()
    }
    Card(
        shape = RoundedCornerShape(19.dp),
        modifier = Modifier
            .padding(horizontal = 18.dp, vertical = 9.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            if (isSystemInDarkTheme()) Color.Black.copy(0.3f) else Color.LightGray.copy(
                0.3f
            )
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(9.dp)
        ) {
            Row(
                modifier = Modifier.align(Alignment.TopStart),
                horizontalArrangement = Arrangement.spacedBy(9.dp)
            ) {
                AndroidView(
                    factory = { context ->
                        ImageView(context).apply {
                            scaleType = ImageView.ScaleType.CENTER_CROP
                        }
                    },
                    update = { imageView ->
                        Glide.with(imageView.context)
                            .load(productItem.image)
                            .into(imageView)
                    },
                    modifier = Modifier
                        .size(126.dp)
                        .clickable {
                            navController.navigate(Screen.detailsWithId(productId = productItem.id))
                        }
                        .clip(
                            RoundedCornerShape(12.dp)
                        )
                )
                Column(
                    modifier = Modifier
                        .height(126.dp)
                        .fillMaxWidth(), verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = productItem.title,
                        maxLines = 2,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                    Text(
                        text = productItem.category,
                        maxLines = 1,
                        color = if (isSystemInDarkTheme()) Color.White.copy(0.5f) else Color.Black.copy(
                            0.5f
                        )

                    )
                    Text(text = "$${productItem.price}", color = Color.Blue, maxLines = 1)
                }
            }
            Icon(
                painter = painterResource(id = R.drawable.delete_icon),
                contentDescription = null,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(27.dp)
                    .clickable {
                        cartViewModel.removeProductFromCart(productId = productItem.id)
                    },
                tint = Color.Red
            )
            ProductQuantity(
                modifier = Modifier.align(Alignment.BottomEnd),
                cartViewModel = cartViewModel,
                cartItem = cartItem
            )
        }
    }
}

@Composable
fun ProductQuantity(
    modifier: Modifier = Modifier,
    cartItem: CartItem,
    cartViewModel: CartViewModel,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = R.drawable.remove_circle),
            contentDescription = null,
            modifier = Modifier
                .size(27.dp)
                .clickable {
                    cartViewModel.decrementValue(cartItem.productId)
                },
            if (isSystemInDarkTheme()) Color.White else Color.Blue.copy(0.5f)
        )
        Text(
            text = "${cartItem.quantity}",
            color = if (isSystemInDarkTheme()) Color.White else Color.Black,
            style = MaterialTheme.typography.titleMedium
        )
        Icon(
            painter = painterResource(id = R.drawable.add_circle),
            contentDescription = null,
            modifier = Modifier
                .size(27.dp)
                .clickable {
                    cartViewModel.incrementValue(cartItem.productId)
                },
            tint = if (isSystemInDarkTheme()) Color.White else Color.Blue.copy(0.5f)
        )
    }
}

@Composable
fun PlaceOrderButton(modifier: Modifier = Modifier,navController: NavHostController, cartViewModel: CartViewModel) {
    val totalCost by cartViewModel.totalCost.collectAsState()
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Total Cost: $${String.format("%.2f", totalCost)}",
            modifier = Modifier
                .padding(start = 18.dp)
                .weight(1f),
            fontWeight = FontWeight.SemiBold,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black
        )
        Button(
            onClick = {
                      navController.navigate(Screen.CheckOut)
            },
            modifier = Modifier
                .padding(end = 18.dp)
                .weight(1f),
            shape = RoundedCornerShape(9.dp),
            colors = ButtonDefaults.buttonColors(if (isSystemInDarkTheme()) Violet else Cinereous)
        ) {
            Text(
                text = "Checkout",
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }
    }
}