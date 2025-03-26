package com.github.theapache64.retrosheetsample

import com.github.theapache64.retrosheet.core.RetrosheetConverter
import com.github.theapache64.retrosheet.core.RetrosheetInterceptor
import com.github.theapache64.retrosheet.core.createRequestInterceptorPlugin
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import java.util.Date
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


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

fun createNotesApi(): NotesApi {
    val config = RetrosheetInterceptor.Builder()
        .setLogging(true)
        // To Read
        .addSheet(
            SHEET_NAME, // sheet name
            "created_at", "title", "description" // columns in same order
        )
        // To write
        .addForm(
            ADD_NOTE_ENDPOINT,
            "https://docs.google.com/forms/d/e/1FAIpQLSdmavg6P4eZTmIu-0M7xF_z-qDCHdpGebX8MGL43HSGAXcd3w/viewform?usp=sf_link" // form link
        )
        .build()

    val jsonConfig = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    val ktorClient = HttpClient {
        install(createRequestInterceptorPlugin(config)) {}
        install(ContentNegotiation) {
            json(jsonConfig)
        }
    }


    val retrofit = Ktorfit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1YTWKe7_mzuwl7AO1Es1aCtj5S9buh3vKauKCMjx1j_M/") // Sheet's public URL
        .httpClient(ktorClient)
        .converterFactories(RetrosheetConverter(config))
        .build()

    return retrofit.createNotesApi()
}