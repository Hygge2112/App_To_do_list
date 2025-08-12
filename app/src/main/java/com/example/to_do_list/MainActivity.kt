package com.example.to_do_list

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.example.to_do_list.navigation.AppNavigation
import com.example.to_do_list.navigation.Routes
import com.example.to_do_list.ui.theme.To_do_listTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : ComponentActivity() {

    // Trình khởi chạy để yêu cầu quyền thông báo (POST_NOTIFICATIONS)
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Quyền đã được cấp, không cần hành động thêm ở đây.
        } else {
            // Quyền bị từ chối. Bạn có thể hiển thị một thông báo
            // giải thích rằng tính năng nhắc nhở sẽ không hoạt động nếu không có quyền.
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Yêu cầu các quyền cần thiết khi khởi động ứng dụng
        askNotificationPermission()
        askToScheduleExactAlarmsPermission() // <-- SỬA ĐỔI: Gọi hàm yêu cầu quyền đặt báo thức

        val auth = Firebase.auth
        val startDestination = if (auth.currentUser != null) {
            Routes.MAIN_SCREEN
        } else {
            Routes.ONBOARDING
        }

        setContent {
            To_do_listTheme {
                AppNavigation(startDestination = startDestination)
            }
        }
    }

    // Hàm để yêu cầu quyền gửi thông báo (cho Android 13+)
    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    // <-- SỬA ĐỔI: Thêm hàm mới để yêu cầu quyền đặt báo thức chính xác -->
    /**
     * Kiểm tra và yêu cầu quyền SCHEDULE_EXACT_ALARM cho Android 12 (S) trở lên.
     * Quyền này rất quan trọng để đảm bảo AlarmManager có thể đặt báo thức chính xác.
     * Nếu chưa được cấp, hàm sẽ điều hướng người dùng đến màn hình cài đặt của ứng dụng.
     */
    private fun askToScheduleExactAlarmsPermission() {
        // Chỉ cần yêu cầu trên Android 12 (API 31) trở lên
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            // Kiểm tra xem ứng dụng đã có quyền chưa
            if (!alarmManager.canScheduleExactAlarms()) {
                // Nếu chưa, tạo một Intent để mở màn hình cài đặt quyền
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivity(intent)
                }
            }
        }
    }
}