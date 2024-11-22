package com.example.quickbuy.viewmodel

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.repository.ProductRepository
import com.example.quickbuy.viewmodel.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(private val productRepository: ProductRepository) : ViewModel() {

    private val _productDetails = MutableStateFlow(
        ProductsItem()
    )
    val productDetails = _productDetails.asStateFlow()

    private val _uiState = MutableLiveData<DetailsState>()
    val uiState : LiveData<DetailsState> = _uiState

    private val _isLiked = MutableStateFlow(false)
    val isLiked = _isLiked.asStateFlow()

    private val _existInCart = MutableStateFlow(false)
    val exitsInCart = _existInCart.asStateFlow()

    private val _showToast = MutableStateFlow(false)
    val showToast = _showToast.asStateFlow()

    @Inject
    lateinit var userId : String


    private suspend fun checkForLike(productId: String){
        _isLiked.value = productRepository.isLiked(userId,productId)
    }

    private suspend fun checkForExitsInCart(productId: String){
        _existInCart.value = productRepository.isProductInCart(userId,productId)
    }

    private suspend fun fetchProductsDetails(productId: String){
        _productDetails.value = productRepository.getProductById(productId)
    }

    fun fetchProduct(id: String) {
        viewModelScope.launch {
            _uiState.value = DetailsState.Loading
            try {
                fetchProductsDetails(id)
                checkForLike(id)
                checkForExitsInCart(id)
                _uiState.value = DetailsState.Success
            } catch (e: Exception) {
                _uiState.value = DetailsState.Error("Error: ${e.message}")
            }
        }
    }

    fun changeLikedState(productId: String){
        _isLiked.value = !_isLiked.value
        if (!_isLiked.value){
            viewModelScope.launch {
                productRepository.removeLikedProduct(userId,productId)
            }
        }else{
            viewModelScope.launch {
                productRepository.addLikedProduct(userId,productId)
            }
        }
    }

    fun addToCartProduct(productId : String){
        _uiState.value = DetailsState.Loading
        viewModelScope.launch {
            try {
                productRepository.addToCart(userId,productId)
                _existInCart.value = true
                _uiState.value = DetailsState.Success
                _showToast.value = true
            }catch (e : Exception){
                _uiState.value = DetailsState.Error("Error while Added to Cart")
            }
        }
    }

    fun showToastSuccess(){
        _showToast.value = false
    }

}

sealed class DetailsState{
    data class Error(var message : String) : DetailsState()
    object Success : DetailsState()
    object Loading : DetailsState()
}