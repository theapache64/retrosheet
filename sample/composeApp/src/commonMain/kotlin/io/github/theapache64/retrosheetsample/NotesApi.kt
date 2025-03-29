package io.github.theapache64.retrosheetsample

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Query
import io.github.theapache64.retrosheet.annotations.Read
import io.github.theapache64.retrosheet.annotations.Write


interface NotesApi {

    @Read("SELECT * WHERE title = :title")
    @GET(SHEET_NAME)
    suspend fun findNoteByTitle(
        @Query("title") title: String
    ): Note

    @Read("SELECT *  ORDER BY A DESC LIMIT 5")
    @GET(SHEET_NAME) // sheet name
    suspend fun getLastFiveItems(): List<Note>

    @Write
    @POST(ADD_NOTE_ENDPOINT) // form name
    suspend fun addNote(
        @Body addNoteRequest: Note
    ): Note

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
    suspend fun addNoteToInvalidSheet(@Body addNoteRequest: Note): Note
}