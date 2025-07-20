package com.projects.attendancemanager.ui.composables

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String) {
    TopAppBar(
        title = { Text(title) }
    )
}

@Preview
@Composable
fun PreviewAppTopBar() {
    AppTopBar(title = "Attendance Manager")
}
