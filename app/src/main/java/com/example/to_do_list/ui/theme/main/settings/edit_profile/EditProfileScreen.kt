package com.example.to_do_list.ui.theme.main.settings.edit_profile

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.to_do_list.R
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

    // --- SỬA LỖI 1: Khai báo kiểu dữ liệu rõ ràng cho imageUri ---
    var imageUri by remember { mutableStateOf<Any?>(currentUser?.photoUrl) }
    var displayName by remember { mutableStateOf(currentUser?.displayName ?: "") }
    var oldPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }

    // --- SỬA LỖI 2: Thêm state để quản lý hiện/ẩn mật khẩu ---
    var isOldPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }


    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            authViewModel.uploadProfileImage(it) {
                Toast.makeText(context, "Cập nhật ảnh đại diện thành công!", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
            Box(contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = imageUri ?: R.drawable.ic_launcher_background,
                    contentDescription = "Profile Picture",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.ic_launcher_background),
                    error = painterResource(id = R.drawable.ic_launcher_background)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Chạm vào ảnh để thay đổi",
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Tên hiển thị") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- SỬA LỖI 2: Thêm chức năng hiện/ẩn cho Mật khẩu cũ ---
            OutlinedTextField(
                value = oldPassword,
                onValueChange = { oldPassword = it },
                label = { Text("Mật khẩu cũ (bắt buộc nếu đổi mật khẩu)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (isOldPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isOldPasswordVisible = !isOldPasswordVisible }) {
                        Icon(
                            imageVector = if (isOldPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- SỬA LỖI 2: Thêm chức năng hiện/ẩn cho Mật khẩu mới ---
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Mật khẩu mới") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (isNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isNewPasswordVisible = !isNewPasswordVisible }) {
                        Icon(
                            imageVector = if (isNewPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // --- SỬA LỖI 2: Thêm chức năng hiện/ẩn cho Xác nhận mật khẩu ---
            OutlinedTextField(
                value = confirmNewPassword,
                onValueChange = { confirmNewPassword = it },
                label = { Text("Xác nhận mật khẩu mới") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { isConfirmPasswordVisible = !isConfirmPasswordVisible }) {
                        Icon(
                            imageVector = if (isConfirmPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility"
                        )
                    }
                }
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
                        authViewModel.reauthenticateAndChangePassword(oldPassword, newPassword) {
                            Toast.makeText(context, "Cập nhật mật khẩu thành công!", Toast.LENGTH_SHORT).show()
                            if (!isNameChanged) navController.popBackStack()
                        }
                    }

                    if (isNameChanged) {
                        authViewModel.updateDisplayName(displayName) {
                            Toast.makeText(context, "Cập nhật tên thành công!", Toast.LENGTH_SHORT).show()
                            if (!isPasswordChanged) navController.popBackStack()
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