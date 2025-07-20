package com.projects.attendancemanager.ui.view

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.projects.attendancemanager.ui.composables.AppTextField
import com.projects.attendancemanager.ui.viewmodel.ForgotPasswordResult
import com.projects.attendancemanager.ui.viewmodel.ForgotPasswordViewModel

@Composable
fun ForgotPasswordScreen(viewModel: ForgotPasswordViewModel = hiltViewModel()) {
    val state by viewModel.forgotPasswordState.collectAsState()

    var email by remember { mutableStateOf("") }
    val isLoading = state is ForgotPasswordResult.Loading
    val errorMessage = (state as? ForgotPasswordResult.Error)?.message

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Use AppTextField instead of TextField
        AppTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reset password button
        Button(
            onClick = { viewModel.resetPassword(email) },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Reset Password")
        }

        // Loading indicator
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        // Show error message if available
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Success message
        if (state is ForgotPasswordResult.Success) {
            Text(
                text = "Password reset email sent successfully.",
                color = Color.Green,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun PreviewForgotPasswordScreen() {
    ForgotPasswordScreen(viewModel = hiltViewModel())
}
