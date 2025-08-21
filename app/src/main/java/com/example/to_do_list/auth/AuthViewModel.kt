package com.example.to_do_list.auth

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    private val storage = Firebase.storage

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()
    val currentUser = auth.currentUser

    fun signUpUser(name: String, email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val user = result.user
                user?.let {
                    // --- SỬA LỖI: Bỏ dấu gạch ngang ở đây ---
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

    fun uploadProfileImage(uri: Uri, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val user = auth.currentUser
                if (user == null) {
                    _uiState.value = AuthUiState(error = "Người dùng không tồn tại.")
                    return@launch
                }

                val storageRef = storage.reference.child("profile_images/${user.uid}")
                storageRef.putFile(uri).await()
                val downloadUrl = storageRef.downloadUrl.await()

                val profileUpdates = userProfileChangeRequest {
                    photoUri = downloadUrl
                }
                user.updateProfile(profileUpdates).await()

                _uiState.value = AuthUiState(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = AuthUiState(error = e.message ?: "Tải ảnh lên thất bại.")
            }
        }
    }

    fun reauthenticateAndChangePassword(oldPassword: String, newPassword: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            try {
                val user = auth.currentUser
                if (user?.email == null) {
                    _uiState.value = AuthUiState(error = "Không tìm thấy thông tin người dùng.")
                    return@launch
                }

                val credential = EmailAuthProvider.getCredential(user.email!!, oldPassword)
                user.reauthenticate(credential).await()
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