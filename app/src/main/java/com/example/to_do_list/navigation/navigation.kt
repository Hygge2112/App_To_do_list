package com.example.to_do_list.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.to_do_list.auth.LoginScreen
import com.example.to_do_list.auth.SignUpScreen
import com.example.to_do_list.introduce.OnboardingScreen
import com.example.to_do_list.ui.theme.add_habit.AddHabitScreen
import com.example.to_do_list.ui.theme.add_habit.habit_details.HabitDetailScreen
import com.example.to_do_list.ui.theme.create_habit.CreateHabitScreen
import com.example.to_do_list.ui.theme.main.MainScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val MAIN_SCREEN = "main_screen"
    const val ADD_HABIT = "add_habit"
    const val HABIT_DETAIL = "habit_detail/{categoryName}"
    // THAY ĐỔI 1: Thêm `iconName` vào route để có thể truyền đi
    const val CREATE_HABIT = "create_habit?habitName={habitName}&categoryName={categoryName}&iconName={iconName}"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onGetStartedClick = { navController.navigate(Routes.SIGNUP) },
                onLoginClick = { navController.navigate(Routes.LOGIN) }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(Routes.MAIN_SCREEN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                navController = navController,
                onSignUpSuccess = {
                    navController.navigate(Routes.MAIN_SCREEN) {
                        popUpTo(navController.graph.startDestinationId) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.MAIN_SCREEN) {
            MainScreen(navController = navController)
        }
        composable(Routes.ADD_HABIT) {
            AddHabitScreen(navController = navController)
        }
        composable(
            route = Routes.HABIT_DETAIL,
            arguments = listOf(navArgument("categoryName") { type = NavType.StringType })
        ) { backStackEntry ->
            val categoryName = backStackEntry.arguments?.getString("categoryName") ?: ""
            HabitDetailScreen(navController = navController, categoryName = categoryName)
        }

        // THAY ĐỔI 2: Cập nhật composable để nhận và xử lý `iconName`
        composable(
            route = Routes.CREATE_HABIT,
            arguments = listOf(
                navArgument("habitName") {
                    type = NavType.StringType
                    nullable = true
                },
                navArgument("categoryName") {
                    type = NavType.StringType
                    nullable = true
                },
                // Thêm argument cho iconName
                navArgument("iconName") {
                    type = NavType.StringType
                    nullable = true
                }
            )
        ) { backStackEntry ->
            // Lấy tất cả các giá trị từ arguments
            val habitName = backStackEntry.arguments?.getString("habitName")
            val categoryName = backStackEntry.arguments?.getString("categoryName")
            val iconName = backStackEntry.arguments?.getString("iconName")

            // Truyền tất cả giá trị vào màn hình CreateHabitScreen
            CreateHabitScreen(
                navController = navController,
                habitName = habitName,
                categoryName = categoryName,
                iconName = iconName
            )
        }
    }
}