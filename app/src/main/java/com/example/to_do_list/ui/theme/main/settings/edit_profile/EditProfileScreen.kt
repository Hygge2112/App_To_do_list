package com.example.to_do_list.ui.theme.main.settings.edit_profile

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.to_do_list.auth.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()
    val currentUser = authViewModel.currentUser
    val context = LocalContext.current

    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var oldPassword by remember { mutableStateOf("") } // <-- THÊM MỚI
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    // Xử lý lỗi
    uiState.error?.let {
        AlertDialog(
            onDismissRequest = { authViewModel.clearError() },
            title = { Text("Lỗi") },
            text = { Text(it) },
            confirmButton = {
                Button(onClick = { authViewModel.clearError() }) {
                    Text("OK")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chỉnh sửa hồ sơ") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Tên hiển thị") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- THÊM MỚI: Ô nhập mật khẩu cũ ---
            OutlinedTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = { Text("Mật khẩu cũ (bắt buộc nếu đổi mật khẩu)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Mật khẩu mới") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = { Text("Xác nhận mật khẩu mới") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    val isNameChanged = displayName != currentUser?.displayName && displayName.isNotBlank()
                    val isPasswordChanged = newPassword.isNotBlank()

                    if (!isNameChanged && !isPasswordChanged) {
                        Toast.makeText(context, "Không có thay đổi nào", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (isPasswordChanged) {
                        if (oldPassword.isBlank()) {
                            Toast.makeText(context, "Vui lòng nhập mật khẩu cũ!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        if (newPassword != confirmNewPassword) {
                            Toast.makeText(context, "Mật khẩu xác nhận không khớp!", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        // --- CẬP NHẬT: Gọi hàm xác thực và thay đổi mật khẩu ---
                        authViewModel.reauthenticateAndChangePassword(oldPassword, newPassword) {
                            Toast.makeText(context, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                            if (!isNameChanged) navController.popBackStack()
                        }
                    }

                    if (isNameChanged) {
                        authViewModel.updateDisplayName(displayName) {
                            Toast.makeText(context, "Cập nhật tên thành công!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                } else {
                    Text("Lưu thay đổi", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}