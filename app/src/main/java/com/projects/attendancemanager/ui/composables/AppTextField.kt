package com.projects.attendancemanager.ui.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun AppTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier // Added modifier parameter with a default value
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.padding(8.dp) // Use the passed modifier here
    )
}

@Preview
@Composable
fun PreviewAppTextField() {
    val textState = remember { mutableStateOf("") }
    AppTextField(
        label = "Enter Text",
        value = textState.value,
        onValueChange = { textState.value = it }
    )
}
