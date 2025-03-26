package com.github.theapache64.retrosheetsample

import com.github.theapache64.retrosheet.annotations.Read
import com.github.theapache64.retrosheet.annotations.Write
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query


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
