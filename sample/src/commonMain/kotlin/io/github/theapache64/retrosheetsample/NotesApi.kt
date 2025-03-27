package io.github.theapache64.retrosheetsample

import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.github.theapache64.retrosheet.annotations.Read
import io.github.theapache64.retrosheet.annotations.Write
import io.github.theapache64.retrosheet.core.RetrosheetConfig
import io.github.theapache64.retrosheet.core.RetrosheetConverter
import io.github.theapache64.retrosheet.core.createRetrosheetPlugin
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json


internal const val SHEET_NAME = "notes"
internal const val ADD_NOTE_ENDPOINT = "add_note"

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

const val GOOGLE_SHEET_PUBLIC_URL = "https://docs.google.com/spreadsheets/d/1YTWKe7_mzuwl7AO1Es1aCtj5S9buh3vKauKCMjx1j_M/"

fun createNotesApi(useProxyForWrite: Boolean = false): NotesApi {
    val config = RetrosheetConfig.Builder()
        .setLogging(true)
        .setUseProxyForWrite(useProxyForWrite)
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
        install(createRetrosheetPlugin(config)) {}
        install(ContentNegotiation) {
            json()
        }
    }

    val retrofit = Ktorfit.Builder()
        .baseUrl(GOOGLE_SHEET_PUBLIC_URL) // Sheet's public URL
        .httpClient(ktorClient)
        .converterFactories(RetrosheetConverter(config))
        .build()

    return retrofit.createNotesApi()
}