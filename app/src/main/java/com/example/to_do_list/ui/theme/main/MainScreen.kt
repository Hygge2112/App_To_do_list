package com.example.to_do_list.ui.theme.main

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.to_do_list.ui.theme.CardDarkBackground
import com.example.to_do_list.ui.theme.TextPrimaryDark
import com.example.to_do_list.ui.theme.main.calendar.CalendarScreen
import com.example.to_do_list.ui.theme.main.home.HomeScreen
import com.example.to_do_list.ui.theme.main.settings.SettingsScreen

sealed class MainScreenItems(val route: String, val icon: ImageVector, val title: String) {
    object Home : MainScreenItems("home", Icons.Default.Home, "Hôm nay")
    object Calendar : MainScreenItems("calendar", Icons.Default.Notifications, "Lịch")
    object Settings : MainScreenItems("settings", Icons.Default.Settings, "Cài đặt")
}

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(navController: NavController) {
    val bottomNavController = rememberNavController()
    val navItems = listOf(
        MainScreenItems.Home,
        MainScreenItems.Calendar,
        MainScreenItems.Settings
    )

    // --- THAY ĐỔI Ở ĐÂY: Thay thế FAB tròn bằng FAB dài ---
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { /* TODO: Mở màn hình thêm nhiệm vụ */ },
                containerColor = CardDarkBackground, // Màu nền xám tối
                contentColor = TextPrimaryDark,      // Màu chữ/icon trắng
                text = { Text("Thêm nhiệm vụ", fontWeight = FontWeight.SemiBold) },
                icon = { Icon(Icons.Default.Add, contentDescription = "Thêm nhiệm vụ") }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        bottomBar = {
            // BottomAppBar bây giờ chỉ dùng để chứa các tab, không cần logic phức tạp
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.background
            ) {
                val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                navItems.forEach { screen ->
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            bottomNavController.navigate(screen.route) {
                                popUpTo(bottomNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        label = { Text(screen.title) },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.primary,
                            selectedTextColor = MaterialTheme.colorScheme.primary,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.background
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = bottomNavController,
            startDestination = MainScreenItems.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(MainScreenItems.Home.route) { HomeScreen(navController = navController) }
            composable(MainScreenItems.Calendar.route) { CalendarScreen() }
            composable(MainScreenItems.Settings.route) { SettingsScreen() }
        }
    }
}