//package com.projects.attendancemanager.ui.view
//
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.painter.Painter
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.unit.dp
//import com.projects.attendancemanager.ui.composables.AppTopBar
//import com.projects.attendancemanager.ui.viewmodel.UserViewModel
//import com.projects.attendancemanager.ui.viewmodel.UserState
//import androidx.hilt.navigation.compose.hiltViewModel
//import com.projects.attendancemanager.R
//
//@Composable
//fun DashboardScreen(userViewModel: UserViewModel = hiltViewModel()) {
//    val userState by userViewModel.userState.collectAsState()
//
//    when (userState) {
//        is UserState.Loading -> {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                CircularProgressIndicator()
//            }
//        }
//        is UserState.Error -> {
//            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                Text(text = (userState as UserState.Error).message)
//            }
//        }
//        is UserState.Success -> {
//            val user = (userState as UserState.Success).user
//
//            Column(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(16.dp),
//                horizontalAlignment = Alignment.CenterHorizontally
//            ) {
//                AppTopBar(title = "Dashboard")
//
//                Spacer(modifier = Modifier.height(20.dp))
//
//                Text(text = "Welcome, ${user.username}!", style = MaterialTheme.typography.headlineSmall)
//                Spacer(modifier = Modifier.height(20.dp))
//                Text(text = "Role: ${user.role}", style = MaterialTheme.typography.bodyMedium)
//                Spacer(modifier = Modifier.height(40.dp))
//
//                DashboardCard(
//                    icon = painterResource(id = R.drawable.ic_attendance), // Custom attendance icon
//                    text = "Attendance Management",
//                    onClick = { /* Navigate to Attendance */ }
//                )
//                Spacer(modifier = Modifier.height(20.dp))
//
//                DashboardCard(
//                    icon = painterResource(id = R.drawable.ic_profile), // Custom profile icon
//                    text = "User Profile",
//                    onClick = { /* Navigate to Profile */ }
//                )
//                Spacer(modifier = Modifier.height(20.dp))
//
//                DashboardCard(
//                    icon = painterResource(id = R.drawable.ic_logout), // Custom logout icon
//                    text = "Sign Out",
//                    onClick = { /* Sign out */ }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun DashboardCard(icon: Painter, text: String, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier.fillMaxWidth(),
//        elevation = CardDefaults.cardElevation(8.dp)
//    ) {
//        Column(
//            modifier = Modifier
//                .padding(16.dp)
//                .fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            IconButton(onClick = onClick) {
//                Icon(painter = icon, contentDescription = text)
//            }
//            Text(text = text)
//        }
//    }
//}
//
//@Preview
//@Composable
//fun PreviewDashboardScreen() {
//    DashboardScreen()
//}
