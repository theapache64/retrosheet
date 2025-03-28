package io.github.theapache64.retrosheetsample

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.io.IOException

@Composable
fun AppUi() {
    MaterialTheme {
        val api = remember { createNotesApi() }
        var notes = remember { mutableStateListOf<Note>() }
        var statusMsg by remember { mutableStateOf("") }

        fun setLoaded() {
            statusMsg = "Last 5 items"
        }

        LaunchedEffect(api) {
            statusMsg = "Loading items..."
            notes.clear()
            notes.addAll(api.getLastFiveItems())
            setLoaded()
        }

        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            var title by remember { mutableStateOf("") }
            var description by remember { mutableStateOf("") }
            var isAdding by remember { mutableStateOf(false) }

            // Control and Tables
            Column {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Title*") },
                        enabled = !isAdding
                    )

                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") },
                        enabled = !isAdding
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val scope = rememberCoroutineScope()
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                statusMsg = "Refreshing..."
                                notes.clear()
                                notes.addAll(api.getLastFiveItems())
                                setLoaded()
                            }
                        },
                        shape = RoundedCornerShape(4.dp),
                        enabled = notes.isNotEmpty()
                    ) {
                        Text("REFRESH")
                    }

                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                if (title.isEmpty()) {
                                    statusMsg = "ERROR: Title can't be empty"
                                    return@launch
                                }

                                try {
                                    statusMsg = "Adding new item..."
                                    isAdding = true
                                    api.addNote(
                                        AddNoteRequest(
                                            title = title,
                                            description = description
                                        )
                                    )
                                    isAdding = false
                                    statusMsg = ""
                                    title = ""
                                    description = ""
                                    delay(1000)
                                    statusMsg = "Refreshing..."
                                    notes.clear()
                                    notes.addAll(api.getLastFiveItems())
                                    setLoaded()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    isAdding = false
                                    statusMsg = "ERROR: ${e.message}"
                                }
                            }
                        },
                        shape = RoundedCornerShape(4.dp),
                        enabled = !isAdding
                    ) {
                        Text("ADD")
                    }
                }

                Text(
                    modifier = Modifier.padding(vertical = 10.dp), text = statusMsg
                )

                LazyColumn {
                    items(notes) { item ->
                        ElevatedCard(
                            modifier = Modifier.padding(bottom = 3.dp)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(start = 10.dp, end = 50.dp, top = 10.dp, bottom = 10.dp)
                            ) {
                                Text(item.title, fontWeight = FontWeight.Bold)
                                Text(item.description ?: "-")
                                Text(
                                    item.createdAt,
                                    fontWeight = FontWeight.Thin,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 12.sp,
                                    color = Color(0xFF888888)
                                )
                            }
                        }
                    }
                }
            }

            // WebPage
        }
    }
}