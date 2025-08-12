package com.example.to_do_list.navigation

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.to_do_list.ai_chat.AIChatScreen
import com.example.to_do_list.auth.LoginScreen
import com.example.to_do_list.auth.SignUpScreen
import com.example.to_do_list.data.HabitViewModel
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
    const val CREATE_HABIT = "create_habit?habitName={habitName}&categoryName={categoryName}&iconName={iconName}"
    const val AI_CHAT = "ai_chat"
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation(startDestination: String) {
    val navController = rememberNavController()
    // <-- SỬA ĐỔI: Khởi tạo HabitViewModel ở đây để có thể chia sẻ cho các màn hình -->
    val habitViewModel: HabitViewModel = viewModel(
        factory = HabitViewModel.HabitViewModelFactory(LocalContext.current.applicationContext as Application)
    )

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

        composable(
            route = Routes.CREATE_HABIT,
            arguments = listOf(
                navArgument("habitName") { type = NavType.StringType; nullable = true },
                navArgument("categoryName") { type = NavType.StringType; nullable = true },
                navArgument("iconName") { type = NavType.StringType; nullable = true }
            )
        ) { backStackEntry ->
            CreateHabitScreen(
                navController = navController,
                habitName = backStackEntry.arguments?.getString("habitName"),
                categoryName = backStackEntry.arguments?.getString("categoryName"),
                iconName = backStackEntry.arguments?.getString("iconName")
            )
        }

        // <-- SỬA ĐỔI: Truyền habitViewModel vào AIChatScreen -->
        composable(Routes.AI_CHAT) {
            AIChatScreen(
                navController = navController,
                habitViewModel = habitViewModel
            )
        }
    }
}