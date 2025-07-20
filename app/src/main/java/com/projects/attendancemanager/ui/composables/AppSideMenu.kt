package com.projects.attendancemanager.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppSideMenu() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Home", modifier = Modifier.padding(8.dp))
        Text("Subjects", modifier = Modifier.padding(8.dp))
        Text("Attendance", modifier = Modifier.padding(8.dp))
    }
}

@Preview
@Composable
fun PreviewAppSideMenu() {
    AppSideMenu()
}
