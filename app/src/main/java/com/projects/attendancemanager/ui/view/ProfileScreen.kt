package com.projects.attendancemanager.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.projects.attendancemanager.ui.viewmodel.LoginState
import com.projects.attendancemanager.ui.viewmodel.LoginViewModel

@Composable
fun ProfileScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
) {
    val loginState by loginViewModel.loginState.collectAsState()

    val welcomeMessage = when (val state = loginState) {
        is LoginState.Success -> {
            val username = state.user.username
            when (state.user.role.lowercase()) {
                "professor" -> "Welcome Professor $username"
                "undergrad student", "undergraduate" -> "Welcome $username (UG)"
                "grad student", "graduate student" -> "Welcome $username (Grad)"
                else -> "Welcome $username"
            }
        }
        else -> "Welcome"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = welcomeMessage, style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                loginViewModel.resetLoginState()
                navController.navigate("login") {
                    popUpTo("profile") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Logout")
        }
    }
}
