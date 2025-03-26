package com.github.theapache64.retrosheetsample.notes

import com.github.theapache64.retrosheet.annotations.Read
import com.github.theapache64.retrosheet.annotations.Write
import com.github.theapache64.retrosheet.core.RetrosheetConverter
import com.github.theapache64.retrosheet.core.RetrosheetInterceptor
import com.github.theapache64.retrosheet.core.createRequestInterceptorPlugin
import com.github.theapache64.retrosheetsample.jsonConfig
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:11
 */
object Notes {

    fun createApi(): NotesApi {
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
}

private const val SHEET_NAME = "notes"
private const val ADD_NOTE_ENDPOINT = "add_note"

interface NotesApi {

    @Read("SELECT * WHERE title = :title")
    @GET(SHEET_NAME)
    suspend fun getNote(
        @Query("title") title: String
    ): Note

    @Read("SELECT *")
    @GET(SHEET_NAME) // sheet name
    suspend fun getNotes(): List<Note>

    @Write
    @POST(ADD_NOTE_ENDPOINT) // form name
    suspend fun addNote(
        @Body addNoteRequest: AddNoteRequest
    ): AddNoteRequest

    /**
     * To test failure scenario.
     */
    @Read("SELECT *")
    @GET("invalid_sheet") // sheet name
    suspend fun getNotesFromInvalidSheet(): List<Note>

    /**
     * To test failure scenario.
     */
    @Write
    @POST("invalid_sheet") // form name
    suspend fun addNoteToInvalidSheet(@Body addNoteRequest: AddNoteRequest): AddNoteRequest
}

fun main() = runBlocking {
    val notesApi = Notes.createApi()
    // println(notesApi.getNotes())

    // Adding sample order
    val addNote = notesApi.addNote(
        AddNoteRequest("Dynamic Note 1", "Dynámic Desc 1: ${java.util.Date()}")
    )

    println(addNote)
}
