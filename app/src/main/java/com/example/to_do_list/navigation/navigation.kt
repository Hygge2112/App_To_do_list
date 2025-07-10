package com.example.to_do_list.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.to_do_list.introduce.OnboardingScreen
import com.example.to_do_list.ui.theme.add_habit.AddHabitScreen
import com.example.to_do_list.ui.theme.add_habit.habit_details.HabitDetailScreen
import com.example.to_do_list.ui.theme.create_habit.CreateHabitScreen
import com.example.to_do_list.ui.theme.main.MainScreen

object Routes {
    const val ONBOARDING = "onboarding"
    const val MAIN_SCREEN = "main_screen"
    const val ADD_HABIT = "add_habit"
    const val HABIT_DETAIL = "habit_detail/{categoryName}"
    const val CREATE_HABIT = "create_habit"
}

// --- THÊM CHÚ THÍCH VÀO ĐÚNG VỊ TRÍ NÀY ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = Routes.ONBOARDING) {
        composable(Routes.ONBOARDING) {
            OnboardingScreen(
                onGetStartedClick = {
                    navController.navigate(Routes.MAIN_SCREEN) {
                        popUpTo(Routes.ONBOARDING) { inclusive = true }
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
        composable(Routes.CREATE_HABIT) {
            CreateHabitScreen(navController = navController)
        }
    }
}
