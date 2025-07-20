package com.projects.attendancemanager.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppNotification(message: String) {
    Snackbar(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(text = message)
    }
}

@Preview
@Composable
fun PreviewAppNotification() {
    AppNotification(message = "Attendance Marked Successfully")
}
