package com.projects.attendancemanager.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.projects.attendancemanager.ui.view.LoginScreen
import com.projects.attendancemanager.ui.view.ProfileScreen
import com.projects.attendancemanager.ui.view.RegisterScreen
import com.projects.attendancemanager.ui.view.ResetNewPasswordScreen
import com.projects.attendancemanager.ui.view.ResetPasswordScreen
import com.projects.attendancemanager.ui.viewmodel.LoginViewModel
import com.projects.attendancemanager.ui.viewmodel.ProfileViewModel
import com.projects.attendancemanager.ui.viewmodel.ResetPasswordViewModel
import com.projects.attendancemanager.ui.viewmodel.UserViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    loginViewModel: LoginViewModel,
    deepLinkEmail: String? = null
) {
    // Handle deep link navigation
    LaunchedEffect(deepLinkEmail) {
        if (!deepLinkEmail.isNullOrEmpty()) {
            navController.navigate("reset-password?email=$deepLinkEmail") {
                // Clear the back stack to prevent going back to login
                popUpTo("login") { inclusive = false }
            }
        }
    }

    NavHost(navController = navController, startDestination = "login") {

        // Login Screen
        composable("login") {
            LoginScreen(
                navController = navController,
                viewModel = loginViewModel
            )
        }

        // Register Screen
        composable("register") {
            val userViewModel: UserViewModel = hiltViewModel()
            RegisterScreen(
                navController = navController,
                viewModel = userViewModel
            )
        }

        // Reset Passworduco.edu Screen
        composable("reset") {
            val resetPasswordViewModel: ResetPasswordViewModel = hiltViewModel()
            ResetPasswordScreen(navController = navController, viewModel = resetPasswordViewModel)

        }

        composable(
            route = "reset-password?email={email}",
            arguments = listOf(navArgument("email") {
                type = NavType.StringType
                defaultValue = ""
                nullable = true
            }),
            deepLinks = listOf(
                navDeepLink { uriPattern = "http://Attendance_Manager/app1/reset-password?email={email}" }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            ResetNewPasswordScreen(navController, email)
        }

        // Profile Screen
        composable("profile") {
            ProfileScreen(
                navController = navController,
                loginViewModel = loginViewModel// ✅ Use the instance, not the class
            )

        }
    }
}