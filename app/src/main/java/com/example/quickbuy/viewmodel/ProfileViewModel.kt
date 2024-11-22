package com.example.quickbuy.viewmodel

import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickbuy.model.UserProfile
import com.example.quickbuy.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val firebaseAuth: FirebaseAuth,
) : ViewModel() {
    private var _userProfile = MutableStateFlow(UserProfile())
    val userProfile = _userProfile.asStateFlow()

    private var _uiState = MutableLiveData<Result>()
    val uiState = _uiState

    private val _showToast = MutableStateFlow(false)
    val showToast = _showToast.asStateFlow()

     suspend fun fetchUserData() {
        _uiState.value = Result.Loading
        val userId = firebaseAuth.currentUser?.uid
        if (userId.isNullOrBlank()){
            _uiState.value = Result.Error("userId is null")
            return
        }
        try {
            val response = productRepository.getUserData(userId)
            _userProfile.value = response
            _uiState.value = Result.Success
        } catch (e: Exception) {
            _uiState.value = Result.Error(e.message.toString())
        }
    }

    fun updateUserData(userProfile: UserProfile){
        _uiState.value = Result.Loading
        viewModelScope.launch {
            try {
                productRepository.addUser(userProfile,userProfile.id)
                _showToast.value = true
                _uiState.value = Result.Success
            }catch (e : Exception){
                _uiState.value = Result.Error(e.message.toString())
            }
        }
    }

    fun showToastSuccess(){
        _showToast.value = false
    }
}

sealed class Result {
    data class Error(val message: String) : Result()
    object Loading : Result()
    object Success : Result()
}