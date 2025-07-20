package com.projects.attendancemanager.ui.composables

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppBottomBar() {
    BottomAppBar(
        modifier = Modifier.padding(8.dp)
    ) {
        // You can add other elements inside the BottomAppBar, such as Text, Icons, etc.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Bottom Bar", modifier = Modifier.padding(16.dp))
        }
    }
}

@Preview
@Composable
fun PreviewAppBottomBar() {
    AppBottomBar()
}
