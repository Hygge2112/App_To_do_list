package com.example.to_do_list

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi // <-- THÊM DÒNG NÀY
import com.example.to_do_list.navigation.AppNavigation
import com.example.to_do_list.navigation.Routes
import com.example.to_do_list.ui.theme.To_do_listTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Kiểm tra trạng thái đăng nhập của người dùng
        val auth = Firebase.auth
        val startDestination = if (auth.currentUser != null) {
            Routes.MAIN_SCREEN
        } else {
            Routes.ONBOARDING
        }

        setContent {
            To_do_listTheme {
                // Truyền màn hình bắt đầu vào AppNavigation
                AppNavigation(startDestination = startDestination)
            }
        }
    }
}