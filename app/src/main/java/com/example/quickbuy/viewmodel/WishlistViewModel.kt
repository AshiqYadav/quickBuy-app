package com.example.quickbuy.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(private val productRepository: ProductRepository) : ViewModel() {

    @Inject
    lateinit var userId : String

    private val _uiState = MutableLiveData<Result>()
    val uiState = _uiState

    private val _likedProductList = MutableStateFlow<List<ProductsItem>>(emptyList())
    val likedProductList = _likedProductList.asStateFlow()

    private val _showToast = MutableStateFlow(false)
    val showToast = _showToast.asStateFlow()

    suspend fun fetchData(){
        _uiState.value = Result.Loading
        try {
            _likedProductList.value = productRepository.getAllLikedProduct(userId)
            _uiState.value = Result.Success
        }catch (e : Exception){
            _uiState.value = Result.Error(e.message.toString())
        }
    }

    fun showToastSuccess(){
        _showToast.value = false
    }

    fun addToCart(productId : String){
        _uiState.value = Result.Loading
        try {
            viewModelScope.launch {
                productRepository.addToCart(userId,productId)
                _uiState.value = Result.Success
                _showToast.value = true
            }
        }catch (e : Exception){
            _uiState.value = Result.Error(e.message.toString())
        }
    }
}