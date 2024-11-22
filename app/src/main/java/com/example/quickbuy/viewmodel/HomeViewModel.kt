package com.example.quickbuy.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickbuy.model.ProductsItem
import com.example.quickbuy.model.UserProfile
import com.example.quickbuy.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val productRepository: ProductRepository) :
    ViewModel() {

    private val TAG = "HomeViewModel"

    private val _products = MutableStateFlow<List<ProductsItem>>(emptyList())
    val products = _products.asStateFlow()

    private val _category = MutableStateFlow<List<String>>(emptyList())
    val category = _category.asStateFlow()

    private val _popularProducts = MutableStateFlow<List<ProductsItem>>(emptyList())
    val popularProducts = _popularProducts.asStateFlow()

    private val _imageSliderList = MutableStateFlow<List<ProductsItem>>(emptyList())
    val imageSliderList = _imageSliderList.asStateFlow()

    private val _uiState = MutableLiveData<Result>()
    val uiState = _uiState

    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    private val _showToast = MutableStateFlow(false)
    val showToast = _showToast.asStateFlow()

    @Inject
    lateinit var userId: String

    init {
        fetchDataFromApi()
    }

    private fun fetchDataFromApi() {
        _uiState.value = Result.Loading
        viewModelScope.launch {
            try {
                getProducts()
                getCategory()
                _uiState.value = Result.Success
            } catch (e: Exception) {
                _uiState.value = Result.Error("${e.message}")
            }
        }
    }

    suspend fun fetchUserProfile() {
        val response = productRepository.getUserData(userId)
        _userProfile.value = response
    }

    private suspend fun getCategory() {
        try {
            val response = productRepository.getCategories()
            if (response.isNotEmpty()) {
                _category.value = response
            }
            Log.d(TAG, "getCategory: ${_category.value}")
        } catch (e: Exception) {
            Log.d(TAG, "getCategory: ${e.message}")
        }
    }

    private suspend fun getProducts() {
        try {
            val response = productRepository.getAllProduct()
            if (response.isNotEmpty()) {
                _products.value = response
                Log.d(TAG, "getProducts: ${_products.value}")
                _popularProducts.value = _products.value.shuffled().take(10)
                _imageSliderList.value = _products.value.shuffled().take(5)
            }
        } catch (e: Exception) {
            Log.d(TAG, "fetchDataFromApi: ${e.message}")
        }
    }

    fun showToastSuccess() {
        _showToast.value = false
    }

    fun addToCart(productId: String) {
        viewModelScope.launch {
            productRepository.addToCart(userId, productId)
            _showToast.value = true
        }
    }
}