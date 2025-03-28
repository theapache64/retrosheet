package io.github.theapache64.retrosheetsample

import androidx.compose.foundation.layout.Row
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun AppUi() {
    MaterialTheme {
        Row {
            var text by remember { mutableStateOf("Hello") }
            TextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("") }
            )
        }
    }
}