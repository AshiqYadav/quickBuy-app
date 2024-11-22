package com.example.quickbuy.screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.quickbuy.R
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.navigation.Screen
import com.example.quickbuy.screen.components.GetCircularProgressLoadingIndicator
import com.example.quickbuy.ui.theme.Violet
import com.example.quickbuy.viewmodel.DetailsState
import com.example.quickbuy.viewmodel.DetailsViewModel

@Composable
fun Details(
    navHostController: NavHostController,
    detailsViewModel: DetailsViewModel = hiltViewModel(),
    productId: String,
) {
    val productDetails by detailsViewModel.productDetails.collectAsState()
    val uiState = detailsViewModel.uiState.observeAsState()
    val TAG = "Details"

    var isLoading by remember {
        mutableStateOf(false)
    }
    var isError by remember {
        mutableStateOf(false)
    }
    var errorMessage by remember {
        mutableStateOf("")
    }
    LaunchedEffect(false) {
        detailsViewModel.fetchProduct(productId)
    }
    LaunchedEffect(uiState.value) {
        when (uiState.value) {
            is DetailsState.Success -> {
                isLoading = false
                isError = false
                Log.d(TAG, "success: $productDetails")
            }

            is DetailsState.Error -> {
                isLoading = false
                isError = true
                errorMessage = (uiState.value as DetailsState.Error).message
                Log.d(TAG, "error: $productDetails")
            }

            is DetailsState.Loading -> {
                isLoading = true
            }

            null -> Unit
        }
    }

    if (isLoading) {
        GetCircularProgressLoadingIndicator()
    } else if (isError) {
        Toast.makeText(LocalContext.current, "Error: $errorMessage", Toast.LENGTH_LONG).show()
    } else {
        DetailsContent(
            product = productDetails,
            navHostController = navHostController,
            detailsViewModel = detailsViewModel
        )
    }
}

@Composable
fun DetailsContent(
    product: ProductsItem,
    navHostController: NavHostController,
    detailsViewModel: DetailsViewModel,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn (contentPadding = PaddingValues(bottom = 27.dp)){
            item {
                ProductImageView(product = product, navHostController = navHostController, detailsViewModel = detailsViewModel)
                DetailsScreenContent(product = product, detailsViewModel = detailsViewModel,navHostController = navHostController)
            }
        }
    }
}

@Composable
fun ProductImageView(product: ProductsItem, navHostController: NavHostController,detailsViewModel: DetailsViewModel) {
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val isLiked by detailsViewModel.isLiked.collectAsState()
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(screenHeight / 2)
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box {
            GlideImage(url = product.image, modifier = Modifier.fillMaxSize())
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp), horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            Color.Gray, shape = CircleShape
                        )
                        .clickable {
                            navHostController.popBackStack()
                        },
                    tint = Color.White

                )
                Icon(
                    imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (isLiked) Color.Transparent else Color.Gray, shape = CircleShape
                        )
                        .clickable {
                            detailsViewModel.changeLikedState(product.id)
                        },
                    tint = if (isLiked) Color.Red else Color.White
                )
            }
        }
    }
}

@Composable
fun DetailsScreenContent(product: ProductsItem, detailsViewModel: DetailsViewModel,navHostController: NavHostController) {
    val showToast by detailsViewModel.showToast.collectAsState()
    if (showToast){
        Toast.makeText(LocalContext.current, "Product successfully added to cart", Toast.LENGTH_SHORT).show()
        detailsViewModel.showToastSuccess()
    }
    val isExitsInCart by detailsViewModel.exitsInCart.collectAsState()
    Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = product.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                modifier = Modifier.width(200.dp)
            )
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.titleMedium,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.Yellow)
            Text(
                text = "${product.rating.rate}",
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
            Text(
                text = "( ${product.rating.count} Review)",
                color = if (isSystemInDarkTheme()) Color.White else Color.Black
            )
        }
        Text(
            text = "Description",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black
        )
        Text(
            text = product.description,
            color = if (isSystemInDarkTheme()) Color.White else Color.Black
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                          navHostController.navigate(Screen.Success)
                },
                colors = ButtonDefaults.buttonColors(if (isSystemInDarkTheme()) Violet else Color.Blue),
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "Buy Now",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Button(
                onClick = {
                    if (!isExitsInCart){
                        detailsViewModel.addToCartProduct(productId = product.id)
                    }else{
                        navHostController.navigate(Screen.cart)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    if (isSystemInDarkTheme()) Violet.copy(0.4f) else Color.Blue.copy(0.4f)
                ),
                modifier = Modifier.width(126.dp)
            ) {
                if (!isExitsInCart){
                    Icon(
                        painter = painterResource(id = R.drawable.add_shopping_cart),
                        contentDescription = null,
                        tint = Color.White
                    )
                }else{
                    Text(text = "Go to cart", color = Color.White, style = MaterialTheme.typography.titleMedium, maxLines = 1)
                }
            }
        }
    }
}