package com.example.to_do_list

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.to_do_list.navigation.AppNavigation
import com.example.to_do_list.ui.theme.To_do_listTheme

class MainActivity : ComponentActivity() {
    // Xóa @RequiresApi(Build.VERSION_CODES.O) khỏi đây
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            To_do_listTheme {
                AppNavigation()
            }
        }
    }
}
