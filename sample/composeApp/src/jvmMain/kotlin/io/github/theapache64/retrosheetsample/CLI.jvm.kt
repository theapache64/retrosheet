package io.github.theapache64.retrosheetsample

import java.util.*

suspend fun main() {
    val notesApi = createNotesApi()
    val notes = notesApi.getLastFiveItems()

    println("Last 5 items")
    for ((index, note) in notes.withIndex()) {
        println("${5 - index}: ${note.title} - ${note.description}")
    }

    val newNote = Note(
        createdAt = null,
        title = "New Note ${Date()}",
        description = "This is a new note"
    )
    notesApi.addNote(newNote)

    println("Added new note")
    val newNoteFromRemote = notesApi.findNoteByTitle(title = newNote.title)
    println("New note from remote: $newNoteFromRemote")
}