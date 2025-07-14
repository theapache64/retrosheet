package io.github.theapache64.retrosheetsample

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
fun AppUi(
    isWeb: Boolean = false
) {
    MaterialTheme {
        val api = remember { createNotesApi() }
        var notes = remember { mutableStateListOf<Note>() }
        var statusMsg by remember { mutableStateOf("") }
        var noteUpdateKeys = remember { mutableStateListOf<String>() }

        fun setLoaded() {
            statusMsg = "Last 5 items"
        }

        LaunchedEffect(api) {
            statusMsg = "Loading items..."
            notes.clear()
            noteUpdateKeys.clear()
            notes.addAll(api.getLastFiveItems())
            // Clear update keys since we're loading fresh data
            repeat(notes.size) { noteUpdateKeys.add("") }
            setLoaded()
        }

        var title by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var isAdding by remember { mutableStateOf(false) }
        var isUpdateMode by remember { mutableStateOf(false) }
        var selectedNoteIndex by remember { mutableStateOf(-1) }

        // Control and Tables
        Column(
            modifier = Modifier.safeDrawingPadding()
                .padding(10.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title*") },
                    enabled = !isAdding,
                    modifier = Modifier.apply {
                        if (!isWeb) {
                            weight(0.5f)
                        }
                    }
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    enabled = !isAdding,
                    modifier = Modifier.apply {
                        if (!isWeb) {
                            weight(0.5f)
                        }
                    }
                )
            }

            // Update mode toggle
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Switch(
                    checked = isUpdateMode,
                    onCheckedChange = { 
                        isUpdateMode = it
                        if (!it) {
                            selectedNoteIndex = -1
                            title = ""
                            description = ""
                        }
                    },
                    enabled = !isAdding
                )
                Text(
                    text = if (isUpdateMode) "Update Mode" else "Add Mode",
                    color = if (isUpdateMode) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
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
                            noteUpdateKeys.clear()
                            notes.addAll(api.getLastFiveItems())
                            // Clear update keys since we're loading fresh data
                            repeat(notes.size) { noteUpdateKeys.add("") }
                            setLoaded()
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    enabled = notes.isNotEmpty()
                ) {
                    Text("REFRESH")
                }

                if (isUpdateMode && selectedNoteIndex >= 0) {
                    OutlinedButton(
                        onClick = {
                            selectedNoteIndex = -1
                            title = ""
                            description = ""
                        },
                        shape = RoundedCornerShape(4.dp),
                        enabled = !isAdding
                    ) {
                        Text("CANCEL")
                    }
                }

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            if (title.isEmpty()) {
                                statusMsg = "ERROR: Title can't be empty"
                                return@launch
                            }

                            try {
                                if (isUpdateMode && selectedNoteIndex >= 0) {
                                    // Update existing note
                                    val updateKey = noteUpdateKeys[selectedNoteIndex]
                                    if (updateKey.isEmpty()) {
                                        statusMsg = "ERROR: This note cannot be updated (no update key)"
                                        return@launch
                                    }
                                    
                                    statusMsg = "Updating note..."
                                    isAdding = true
                                    
                                    api.updateNote(
                                        updateKey,
                                        Note(
                                            title = title,
                                            description = description
                                        )
                                    )
                                    
                                    // Update the note in the list
                                    notes[selectedNoteIndex] = Note(
                                        title = title,
                                        description = description,
                                        createdAt = notes[selectedNoteIndex].createdAt
                                    )
                                    
                                    isAdding = false
                                    statusMsg = "Note updated successfully"
                                    title = ""
                                    description = ""
                                    selectedNoteIndex = -1
                                    isUpdateMode = false
                                } else {
                                    // Add new note
                                    statusMsg = "Adding new item..."
                                    isAdding = true
                                    
                                    val updateKey = api.addNoteForUpdate(
                                        Note(
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
                                    
                                    // Refresh and store update keys
                                    val newNotes = api.getLastFiveItems()
                                    notes.clear()
                                    notes.addAll(newNotes)
                                    
                                    // Add the update key for the new note (assuming it's the first one)
                                    noteUpdateKeys.clear()
                                    noteUpdateKeys.add(updateKey)
                                    repeat(notes.size - 1) { noteUpdateKeys.add("") }
                                    
                                    setLoaded()
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                                isAdding = false
                                statusMsg = "ERROR: ${e.message}"
                            }
                        }
                    },
                    shape = RoundedCornerShape(4.dp),
                    enabled = !isAdding && (!isUpdateMode || selectedNoteIndex >= 0)
                ) {
                    Text(if (isUpdateMode) "UPDATE" else "ADD")
                }
            }

            Text(
                modifier = Modifier.padding(vertical = 10.dp), 
                text = if (isUpdateMode && selectedNoteIndex == -1 && statusMsg.isEmpty()) {
                    "Tap a note to edit it (green dot indicates updatable notes)"
                } else {
                    statusMsg
                }
            )

            LazyColumn {
                items(notes.size) { index ->
                    val item = notes[index]
                    ElevatedCard(
                        modifier = Modifier
                            .padding(bottom = 3.dp)
                            .then(
                                if (isUpdateMode) {
                                    Modifier.clickable {
                                        selectedNoteIndex = index
                                        title = item.title
                                        description = item.description ?: ""
                                    }
                                } else {
                                    Modifier
                                }
                            ),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = if (isUpdateMode && selectedNoteIndex == index) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        )
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(start = 10.dp, end = 50.dp, top = 10.dp, bottom = 10.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(item.title, fontWeight = FontWeight.Bold)
                                if (isUpdateMode && noteUpdateKeys.getOrNull(index)?.isNotEmpty() == true) {
                                    Text(
                                        "●",
                                        color = Color(0xFF4CAF50),
                                        fontSize = 8.sp
                                    )
                                }
                            }
                            Text(item.description ?: "-")
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    item.createdAt ?: "-",
                                    fontWeight = FontWeight.Thin,
                                    fontStyle = FontStyle.Italic,
                                    fontSize = 12.sp,
                                    color = Color(0xFF888888)
                                )
                                if (isUpdateMode) {
                                    Text(
                                        if (noteUpdateKeys.getOrNull(index)?.isNotEmpty() == true) "Updatable" else "Read-only",
                                        fontWeight = FontWeight.Light,
                                        fontSize = 10.sp,
                                        color = if (noteUpdateKeys.getOrNull(index)?.isNotEmpty() == true) Color(0xFF4CAF50) else Color(0xFF888888)
                                    )
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}