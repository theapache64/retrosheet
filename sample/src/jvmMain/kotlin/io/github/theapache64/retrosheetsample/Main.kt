package io.github.theapache64.retrosheetsample

import io.github.theapache64.retrosheet.core.RetrosheetConfig
import io.github.theapache64.retrosheet.core.RetrosheetConverter
import io.github.theapache64.retrosheet.core.createRetrosheetPlugin
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import java.util.Date
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
    val notesApi = createNotesApi()
    // Get all notes
    println("1️⃣ : Getting all notes")
    println(notesApi.getNotes())

    // Adding sample order
    println("2️⃣ : Adding a new note")
    val addNoteRequest = AddNoteRequest(title = "Dynamic Title 1 ${Date()}", description = "Dynámic Desc 1: ${Date()}")
    notesApi.addNote(addNoteRequest)

    // Get note by ID
    println("3️⃣ : Getting newly added note")
    val addedNote = notesApi.getNote(title = addNoteRequest.title)
    println(addedNote)
}
