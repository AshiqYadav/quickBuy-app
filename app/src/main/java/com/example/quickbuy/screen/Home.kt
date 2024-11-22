package com.example.quickbuy.screen

import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.Glide
import com.example.quickbuy.R
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.navigation.Screen
import com.example.quickbuy.screen.components.GetCircularProgressLoadingIndicator
import com.example.quickbuy.ui.theme.Manatee
import com.example.quickbuy.viewmodel.HomeViewModel
import com.example.quickbuy.viewmodel.Result
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.calculateCurrentOffsetForPage
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.delay
import kotlinx.coroutines.yield
import kotlin.math.absoluteValue

@Composable
fun Home(navHostController: NavHostController, homeViewModel: HomeViewModel = hiltViewModel()) {
    val uiState by homeViewModel.uiState.observeAsState()
    when (val state = uiState) {
        is Result.Loading -> GetCircularProgressLoadingIndicator()
        is Result.Error -> Toast.makeText(
            LocalContext.current,
            "error : ${state.message}",
            Toast.LENGTH_SHORT
        ).show()

        is Result.Success -> ShowHomeContent(navHostController, homeViewModel)
        else -> Unit
    }
}

@Composable
fun ShowHomeContent(navHostController: NavHostController, homeViewModel: HomeViewModel) {
    val products by homeViewModel.products.collectAsState()
    val popularProducts by homeViewModel.popularProducts.collectAsState()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 96.dp)
    ) {
        item {
            ProfileHeader(homeViewModel = homeViewModel)
        }
        item {
            ImageSlider(navHostController, homeViewModel)
            Products(
                title = "Popular Products",
                category = popularProducts,
                navHostController = navHostController
            )
            Products(title = "Products", category = products, navHostController = navHostController)
        }
    }
}

@Composable
fun ProfileHeader(homeViewModel: HomeViewModel) {
    val userProfile by homeViewModel.userProfile.collectAsState()
    LaunchedEffect(Unit) {
        homeViewModel.fetchUserProfile()
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            AndroidView(
                factory = { context ->
                    ImageView(context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                    }
                },
                update = { imageView ->
                    Glide.with(imageView.context)
                        .load(userProfile.profilePictureUrl)
                        .placeholder(R.drawable.img)
                        .error(R.drawable.img)
                        .into(imageView)
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hello",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
                Text(
                    text = userProfile.username,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black
                )
            }
        }
        Icon(
            painter = painterResource(id = R.drawable.notification),
            contentDescription = "notification icon",
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .background(Color.LightGray.copy(0.1f))
        )
    }
}

@OptIn(ExperimentalPagerApi::class)
@Composable
fun ImageSlider(navHostController: NavHostController, homeViewModel: HomeViewModel) {
    val imageSliderList by homeViewModel.imageSliderList.collectAsState()
    if (imageSliderList.isEmpty()) return
    val pagerState = rememberPagerState(initialPage = 0)
    LaunchedEffect(Unit) {
        while (true) {
            yield()
            delay(2600)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1) % (pagerState.pageCount)
            )
        }
    }
    val indicatorColor = if (isSystemInDarkTheme()) Color.White else Color.Black
    Column {
        HorizontalPager(
            count = imageSliderList.size,
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
        ) { page ->
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .graphicsLayer {
                        val pageOffset = calculateCurrentOffsetForPage(page).absoluteValue

                        lerp(
                            start = 0.85f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        ).also { scale ->
                            scaleX = scale
                            scaleY = scale
                        }

                        alpha = lerp(
                            start = 0.5f,
                            stop = 1f,
                            fraction = 1f - pageOffset.coerceIn(0f, 1f)
                        )
                    }
                    .clickable {
                        navHostController.navigate(Screen.detailsWithId(imageSliderList[page].id))
                    }
            ) {
                val imageUrl = imageSliderList[page].image
                    .removePrefix("[\"")
                    .removeSuffix("\"]")
                GlideImage(
                    imageUrl, Modifier.height(200.dp)
                )
            }
        }
        HorizontalPagerIndicator(
            pagerState = pagerState,
            activeColor = indicatorColor,
            inactiveColor = indicatorColor.copy(0.3f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(16.dp)
        )
    }
}

@Composable
fun Products(title: String, category: List<ProductsItem>, navHostController: NavHostController) {
    var visibility by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(true) {
        visibility = true
    }
    Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .weight(3f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .weight(1f)
                    .background(Manatee.copy(0.4f), shape = RoundedCornerShape(5.dp))
                    .border(
                        BorderStroke(0.5.dp, if (isSystemInDarkTheme()) Color.White.copy(0.7f) else Color.Black),
                        shape = RoundedCornerShape(5.dp)
                    )
                    .clickable {
                        navHostController.navigate(Screen.showAllProductWithTitle(title = title))
                    }
            ) {
                Text(
                    text = "Show All",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(27.dp),
                    tint = if (isSystemInDarkTheme()) Color.White else Color.DarkGray,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        AnimatedVisibility(visible = visibility, enter = expandVertically()) {
            LazyRow {
                items(category) { category ->
                    ProductItem(category, navHostController = navHostController)
                }
            }
        }
    }
}

@Composable
fun ProductItem(product: ProductsItem, navHostController: NavHostController) {
    Log.d("ProductItem", "Image URL: ${product.image}")
    Card(
        modifier = Modifier
            .padding(horizontal = 8.dp)
            .size(124.dp, 148.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray.copy(0.3f)),
        onClick = {
            navHostController.navigate(Screen.detailsWithId(product.id))
        }
    ) {
        Column {
            GlideImage(url = product.image, Modifier.height(96.dp))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = product.title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = 8.dp),
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                maxLines = 1
            )
            Text(
                text = "$${product.price}",
                style = MaterialTheme.typography.bodyMedium,
                color = if (isSystemInDarkTheme()) Color.White else Color.Black,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun GlideImage(url: String, modifier: Modifier = Modifier) {
    AndroidView(
        modifier = modifier
            .fillMaxWidth(),
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        },
        update = { imageView ->
            Glide.with(imageView.context)
                .load(url)
                .placeholder(R.drawable.img)
                .error(R.drawable.img)
                .into(imageView)
        }
    )
}