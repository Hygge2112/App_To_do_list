package com.example.to_do_list.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel : ViewModel() {

    private val auth: FirebaseAuth = Firebase.auth
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()
    val currentUser = auth.currentUser

    // Các hàm signUpUser, loginUser, updateDisplayName giữ nguyên...
    fun signUpUser(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                user?.let {
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    it.updateProfile(profileUpdates).await()
                }
                _uiState.value = AuthUiState(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Đã có lỗi xảy ra")
            }
        }
    }

    fun loginUser(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                auth.signInWithEmailAndPassword(email, password).await()
                _uiState.value = AuthUiState(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Đã có lỗi xảy ra")
            }
        }
    }

    fun updateDisplayName(newName: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val profileUpdates = userProfileChangeRequest {
                    displayName = newName
                }
                auth.currentUser?.updateProfile(profileUpdates)?.await()
                _uiState.value = AuthUiState(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Lỗi cập nhật tên")
            }
        }
    }

    // --- CẬP NHẬT: Thay thế hàm updatePassword bằng hàm mới an toàn hơn ---
    /**
     * Xác thực lại người dùng bằng mật khẩu cũ, sau đó cập nhật mật khẩu mới.
     * Đây là phương pháp bảo mật được Firebase khuyến nghị.
     */
    fun reauthenticateAndChangePassword(oldPassword: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val user = auth.currentUser
                if (user?.email == null) {
                    _uiState.value = AuthUiState(error = "Không tìm thấy thông tin người dùng.")
                    return@launch
                }

                // 1. Tạo thông tin xác thực với email và mật khẩu cũ
                val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)

                // 2. Xác thực lại người dùng
                user.reauthenticate(credential).await()

                // 3. Nếu xác thực thành công, cập nhật mật khẩu mới
                user.updatePassword(newPassword).await()

                _uiState.value = AuthUiState(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = "Mật khẩu cũ không chính xác hoặc đã có lỗi xảy ra.")
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null, isLoading = false)
    }

    fun logoutUser() {
        viewModelScope.launch {
            auth.signOut()
            _uiState.value = AuthUiState()
        }
    }
}