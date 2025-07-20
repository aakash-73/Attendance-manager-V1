package com.projects.attendancemanager.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppButton(text: String, onClick: () -> Unit) {
    Button(onClick = onClick, modifier = Modifier.padding(8.dp)) {
        Text(text)
    }
}

@Preview
@Composable
fun PreviewAppButton() {
    AppButton(text = "Click Me") {}
}
