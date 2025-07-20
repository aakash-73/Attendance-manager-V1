package com.projects.attendancemanager.ui.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.navigation.NavController
import com.projects.attendancemanager.R
import com.projects.attendancemanager.ui.viewmodel.LoginState
import com.projects.attendancemanager.ui.viewmodel.LoginViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun LoginScreen(navController: NavController, viewModel: LoginViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginState by viewModel.loginState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    var isKeyboardVisible by remember { mutableStateOf(false) }

    LaunchedEffect(view) {
        snapshotFlow {
            ViewCompat.getRootWindowInsets(view)
                ?.isVisible(Type.ime()) == true
        }.distinctUntilChanged()
            .collectLatest { isVisible -> isKeyboardVisible = isVisible }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .windowInsetsPadding(WindowInsets.ime),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (!isKeyboardVisible) {
                item {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .height(200.dp)
                            .fillMaxWidth(),
                        contentScale = ContentScale.Fit
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            item {
                Text(
                    text = "Login",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email or Username") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { viewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            item {
                when (loginState) {
                    is LoginState.Loading -> CircularProgressIndicator()
                    is LoginState.Error -> Text(
                        text = (loginState as LoginState.Error).message,
                        color = MaterialTheme.colorScheme.error
                    )
                    is LoginState.Success -> {
                        LaunchedEffect(Unit) {
                            Toast.makeText(context, "Login successful", Toast.LENGTH_SHORT).show()
                            navController.navigate("profile") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    }
                    else -> Unit
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row {
                    Text(
                        text = "Forgot password?",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Navigating to PasswordReset", Toast.LENGTH_SHORT).show()
                            navController.navigate("reset")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row {
                    Text("New User? ")
                    Text(
                        text = "Register",
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable {
                            Toast.makeText(context, "Navigating to Register", Toast.LENGTH_SHORT).show()
                            navController.navigate("register")
                        }
                    )
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}
