package com.example.quickbuy.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickbuy.model.UserProfile
import com.example.quickbuy.repository.ProductRepository
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val auth: FirebaseAuth,private val productRepository: ProductRepository) : ViewModel() {

    private val _uiState = MutableLiveData<AuthState>()
    val uiState : LiveData<AuthState> = _uiState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus() {
        if (auth.currentUser == null) {
            _uiState.value = AuthState.Unauthenticated
        } else {
            _uiState.value = AuthState.Authenticated
        }
    }

    fun login(email: String, password: String) {
        _uiState.value = AuthState.Loading

        val validationResult = validateCredentials(email = email, password = password)
        if (validationResult != null) {
            _uiState.value = AuthState.Error(validationResult)
            return
        }

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _uiState.value = AuthState.Authenticated
            } else {
                _uiState.value = AuthState.Error("Sign-in failed: ${it.exception?.message}")
            }
        }
    }

    fun signUp(email: String, password: String, userName: String) {
        _uiState.value = AuthState.Loading

        val validationResult = validateCredentials(email, password, userName)
        if (validationResult != null) {
            _uiState.value = AuthState.Error(validationResult)
            return
        }

        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                viewModelScope.launch {
                    registerUser(email = email, userName = userName)
                }
                _uiState.value = AuthState.Authenticated
            } else {
                _uiState.value = AuthState.Error("Sign-up failed: ${it.exception?.message}")
            }
        }
    }

    private fun validateCredentials(email: String, password: String, userName: String? = null): String? {
        val TAG = "validateCredentials"
        Log.d(TAG, "validateCredentials: enter")
        if (userName != null && userName.isBlank()) {
            Log.d(TAG, "validateCredentials: username")
            return "Username can't be empty"
        }
        Log.d(TAG, "validateCredentials: enter")
        if (email.isBlank()) {
            Log.d(TAG, "validateCredentials: email")
            return "Email can't be empty"
        }
        Log.d(TAG, "validateCredentials: enter")
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Log.d(TAG, "validateCredentials: invalid")
            return "Invalid email format"
        }
        if (password.isBlank()) {
            return "Password can't be empty"
        }
        if (password.length < 6) {
            return "Password must be at least 6 characters long"
        }

        Log.d(TAG, "validateCredentials: enter")
        if (!password.any { it.isDigit() }) {
            Log.d(TAG, "validateCredentials: success")
            return "Password must contain at least one digit"
        }
        Log.d(TAG, "validateCredentials: exit")
        if (!password.any { it.isLetter() }) {
            return "Password must contain at least one letter"
        }
        return null
    }

    fun signOut(){
        _uiState.value = AuthState.Loading
        auth.signOut()
        _uiState.value = AuthState.Unauthenticated
    }

    private suspend fun registerUser(email : String, userName : String){
        val userId = auth.currentUser?.uid ?: return
        val userProfile : UserProfile = UserProfile(id = userId,email = email, username = userName)
        productRepository.addUser(userProfile,userId)
    }

}

sealed class AuthState {
    data class Error(val message: String) : AuthState()
    object Authenticated : AuthState()
    object Loading : AuthState()
    object Unauthenticated : AuthState()
}