package com.example.quickbuy.screen

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.quickbuy.R
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.navigation.Screen
import com.example.quickbuy.viewmodel.HomeViewModel

@Composable
fun ShowAllProducts(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel(),
    title : String
) {

    val productList by when (title) {
        "Products" -> homeViewModel.products.collectAsState()
        "Popular Products" -> homeViewModel.popularProducts.collectAsState()
        else -> remember {
            mutableStateOf(emptyList())
        }
    }
    if (productList.isNotEmpty()) {
        ShowAllScreenContent(
            navHostController = navController,
            homeViewModel = homeViewModel,
            title = title,
            productList = productList
        )
    }
}

@Composable
fun ShowAllScreenContent(
    homeViewModel: HomeViewModel,
    navHostController: NavHostController,
    title: String,
    productList : List<ProductsItem>
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(top = 16.dp, bottom = 27.dp, end = 16.dp, start = 16.dp),
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        item(span = { GridItemSpan(2) }) {
            ShowAllProductsHeader(navHostController = navHostController,title)
        }
        items(productList) { product ->
            ProductCardView(
                navHostController = navHostController,
                product = product,
                homeViewModel = homeViewModel
            )
        }
    }
}

@Composable
fun ShowAllProductsHeader(navHostController: NavHostController,title : String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(18.dp)
    ) {
        Icon(
            painter = painterResource(id = R.drawable.arrow_backward),
            contentDescription = null,
            modifier = Modifier
                .size(27.dp)
                .align(Alignment.TopStart)
                .clickable {
                    navHostController.popBackStack()
                },
            if (isSystemInDarkTheme()) Color.White else Color.Black
        )

        Text(
            text = title,
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
    navHostController: NavHostController,
    product: ProductsItem,
    homeViewModel: HomeViewModel,
) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .size(124.dp, 148.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(0.3f)),
        onClick = {
            navHostController.navigate(Screen.detailsWithId(product.id))
        }
    ) {

        val showToast by homeViewModel.showToast.collectAsState()
        if (showToast) {
            Toast.makeText(
                LocalContext.current,
                "Product successfully add to cart",
                Toast.LENGTH_SHORT
            ).show()
            homeViewModel.showToastSuccess()
        }
        Column {
            GlideImage(url = product.image, modifier = Modifier.height(96.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.title,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        maxLines = 1,
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                    Text(
                        text = "$${product.price}",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp),
                        color = if (isSystemInDarkTheme()) Color.White else Color.Black
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.add_circle),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(27.dp)
                        .clickable {
                            homeViewModel.addToCart(productId = product.id)
                        },
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Blue.copy(0.5f)
                )
            }
        }
    }
}