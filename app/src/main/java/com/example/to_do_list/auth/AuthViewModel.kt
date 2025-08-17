package com.example.to_do_list.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// Đại diện cho trạng thái của giao diện xác thực
data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    /** Đăng ký */
    fun signUpUser(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Đã có lỗi xảy ra")
            }
        }
    }

    /** Đăng nhập */
    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Đã có lỗi xảy ra")
            }
        }
    }

    /** Xóa lỗi */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, isLoading = false)
    }

    /** Đăng xuất người dùng hiện tại */
    fun logoutUser() {
        viewModelScope.launch {
            try {
                auth.signOut()
            } finally {
                _uiState.value = AuthUiState() // reset state
            }
        }
    }
}
