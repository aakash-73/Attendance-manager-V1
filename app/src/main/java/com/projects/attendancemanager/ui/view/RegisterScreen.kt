package com.projects.attendancemanager.ui.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.navigation.NavHostController
import com.projects.attendancemanager.R
import com.projects.attendancemanager.ui.composables.AppDropdown
import com.projects.attendancemanager.ui.viewmodel.UserState
import com.projects.attendancemanager.ui.viewmodel.UserViewModel

@Composable
fun RegisterScreen(navController: NavHostController, viewModel: UserViewModel) {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("professor") }

    val userState by viewModel.userState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
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
        // Title
        Text(
            text = "Register",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Input Fields
        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Dropdown for Role
        AppDropdown(
            options = listOf("professor", "undergrad student", "Grad student"),
            selectedOption = selectedRole,
            onOptionSelected = { selectedRole = it }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Register Button
        Button(
            onClick = {
                viewModel.registerUser(username, email, password, selectedRole)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Register")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // State display
        when (userState) {
            is UserState.Loading -> CircularProgressIndicator()
            is UserState.Error -> Text(
                text = (userState as UserState.Error).message,
                color = MaterialTheme.colorScheme.error
            )
            is UserState.Success -> {
                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                LaunchedEffect(Unit) {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
                Text("Registration successful!", color = MaterialTheme.colorScheme.primary)
            }
            else -> Unit
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Text("Existing user? ")
            Text(
                "Login",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
    }
}
