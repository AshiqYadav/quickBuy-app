package com.example.quickbuy.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickbuy.model.CartItem
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    private val _uiState = MutableLiveData<Result>()
    val uiState = _uiState

    private val _cartProductList = MutableStateFlow<List<ProductsItem>>(emptyList())

    private val _cartProductQuantity = MutableStateFlow<List<CartItem>>(emptyList())

    private val _mergedProductList =
        MutableStateFlow<List<Pair<ProductsItem, CartItem>>>(emptyList())
    val mergedProductList: StateFlow<List<Pair<ProductsItem, CartItem>>> =
        _mergedProductList.asStateFlow()

    private val _showToast = MutableStateFlow(false)
    val showToast = _showToast.asStateFlow()

    val totalCost: StateFlow<Double> = _mergedProductList.map { mergedList ->
        mergedList.sumOf { (product, cartItem) ->
            product.price * cartItem.quantity
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = 0.0
    )


    @Inject
    lateinit var userid: String

    private fun fetchCartData() {
        if (userid.isBlank()) {
            _uiState.value = Result.Error("User ID not available")
            return
        }
        _uiState.value = Result.Loading
        viewModelScope.launch {
            try {
                val cartItems = productRepository.getCartProductQuantity(userId = userid)
                val products = productRepository.getAllCartProduct(userId = userid)

                _cartProductList.value = products
                _cartProductQuantity.value = cartItems

                _mergedProductList.value = products.mapNotNull { product ->
                    val cartItem = cartItems.find { it.productId == product.id }
                    cartItem?.let { Pair(product, it) }
                }
                _uiState.value = Result.Success
            } catch (e: Exception) {
                _uiState.value = Result.Error(e.message.toString())
            }
        }
    }

    fun refreshCart() {
        fetchCartData()
    }

    fun incrementValue(productId: String) {
        _cartProductQuantity.value = _cartProductQuantity.value.map { cartItem ->
            if (cartItem.productId == productId) {
                val updatedCartItem = cartItem.copy(quantity = cartItem.quantity + 1)
                viewModelScope.launch {
                    productRepository.incrementProductQuantity(userid, updatedCartItem)
                }
                updatedCartItem
            } else {
                cartItem
            }
        }
        updateMergedProductList()
    }


    fun decrementValue(productId: String) {
        _cartProductQuantity.value = _cartProductQuantity.value.map { cartItem ->
            if (cartItem.productId == productId && cartItem.quantity > 1) {
                val updatedCartItem = cartItem.copy(quantity = cartItem.quantity - 1)
                viewModelScope.launch {
                    productRepository.incrementProductQuantity(userid, updatedCartItem)
                }
                updatedCartItem
            } else {
                cartItem
            }
        }
        updateMergedProductList()
    }


    fun removeProductFromCart(productId: String) {
        _uiState.value = Result.Loading
        try {
            viewModelScope.launch {
                productRepository.removeFromCart(userid, productId)
                refreshCart()
                _showToast.value = true
            }
        } catch (e: Exception) {
            _uiState.value = Result.Error("error : ${e.message}")
        }
    }

    private fun updateMergedProductList() {
        val products = _cartProductList.value
        val cartItems = _cartProductQuantity.value

        _mergedProductList.value = products.mapNotNull { product ->
            val cartItem = cartItems.find { it.productId == product.id }
            cartItem?.let { Pair(product, it) }
        }
    }

    fun showToastSuccess(){
        _showToast.value = false
    }

}

