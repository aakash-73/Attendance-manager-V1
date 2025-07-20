package com.projects.attendancemanager.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.projects.attendancemanager.ui.viewmodel.ResetPasswordViewModel
import com.projects.attendancemanager.ui.viewmodel.ResetPasswordState
import kotlinx.coroutines.launch

@Composable
fun ResetNewPasswordScreen(navController: NavController, email: String) {
    val scope = rememberCoroutineScope()

    val viewModel: ResetPasswordViewModel = hiltViewModel()
    val state by viewModel.resetPasswordState.collectAsState()

    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }
    var successMsg by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Observe success or error messages from ViewModel
    LaunchedEffect(state) {
        when (state) {
            is ResetPasswordState.Loading -> {
                isLoading = true
                errorMsg = null
                successMsg = null
            }
            is ResetPasswordState.Success -> {
                isLoading = false
                successMsg = "Password reset successfully!"
                kotlinx.coroutines.delay(1500)
                navController.navigate("login") {
                    popUpTo("reset-password") { inclusive = true }
                }
            }
            is ResetPasswordState.Error -> {
                isLoading = false
                errorMsg = (state as ResetPasswordState.Error).message
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Reset Password for $email", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = newPassword,
            onValueChange = { newPassword = it },
            label = { Text("New Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )

        Spacer(modifier = Modifier.height(16.dp))

        errorMsg?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        successMsg?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(
            enabled = !isLoading,
            onClick = {
                errorMsg = null
                successMsg = null

                if (newPassword.isBlank() || confirmPassword.isBlank()) {
                    errorMsg = "Please fill in both fields"
                    return@Button
                }

                if (newPassword != confirmPassword) {
                    errorMsg = "Passwords do not match"
                    return@Button
                }

                // 🔄 Use ViewModel to handle password update
                viewModel.submitNewPassword(email, newPassword)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoading) CircularProgressIndicator(modifier = Modifier.size(24.dp)) else Text("Reset Password")
        }
    }
}
