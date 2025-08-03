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

    /**
     * Đăng ký người dùng mới bằng email và mật khẩu.
     * @param email Email của người dùng.
     * @param password Mật khẩu của người dùng.
     * @param onSuccess Callback được gọi khi đăng ký thành công.
     */
    fun signUpUser(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.createUserWithEmailAndPassword(email, password).await()
                // Đăng ký thành công, gọi callback để điều hướng
                onSuccess()
            } catch (e: Exception) {
                // Nếu có lỗi, cập nhật trạng thái lỗi
                _uiState.value = AuthUiState(error = e.message ?: "Đã có lỗi xảy ra")
            }
        }
    }

    /**
     * Đăng nhập người dùng bằng email và mật khẩu.
     * @param email Email của người dùng.
     * @param password Mật khẩu của người dùng.
     * @param onSuccess Callback được gọi khi đăng nhập thành công.
     */
    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                // Đăng nhập thành công, gọi callback để điều hướng
                onSuccess()
            } catch (e: Exception) {
                // Nếu có lỗi, cập nhật trạng thái lỗi
                _uiState.value = AuthUiState(error = e.message ?: "Đã có lỗi xảy ra")
            }
        }
    }

    /**
     * Đặt lại trạng thái lỗi sau khi đã hiển thị.
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, isLoading = false)
    }
}
