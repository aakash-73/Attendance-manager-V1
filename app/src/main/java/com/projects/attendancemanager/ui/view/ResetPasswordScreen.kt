package com.projects.attendancemanager.ui.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.projects.attendancemanager.R
import com.projects.attendancemanager.ui.viewmodel.ResetPasswordState
import com.projects.attendancemanager.ui.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordScreen(navController: NavController, viewModel: ResetPasswordViewModel) {
    var email by remember { mutableStateOf("") }
    val state by viewModel.resetPasswordState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .wrapContentHeight()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "App Logo",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth(),
                    contentScale = ContentScale.Fit
            )

            Text(
                text = "Reset Password",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your registered email") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.resetPassword(email) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Send Reset Link")
            }

            Spacer(modifier = Modifier.height(24.dp))

            when (state) {
                is ResetPasswordState.Loading -> CircularProgressIndicator()
                is ResetPasswordState.Success -> {
                    LaunchedEffect(Unit) {
                        Toast.makeText(context, "Reset link sent to your email", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                }
                is ResetPasswordState.Error -> Text(
                    text = (state as ResetPasswordState.Error).message,
                    color = MaterialTheme.colorScheme.error
                )
                else -> Unit
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Back to Login",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.popBackStack()
                }
            )
        }
    }
}
