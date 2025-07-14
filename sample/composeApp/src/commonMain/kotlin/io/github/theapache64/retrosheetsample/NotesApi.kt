package io.github.theapache64.retrosheetsample

import de.jensklingenberg.ktorfit.http.*
import io.github.theapache64.retrosheet.annotations.Read
import io.github.theapache64.retrosheet.annotations.Update
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
    @POST(WRITE_NOTE_ENDPOINT) // form name
    suspend fun addNote(
        @Body note: Note
    ): Note

    @Write
    @POST(WRITE_NOTE_ENDPOINT) // form name
    suspend fun addNoteForUpdate(
        @Body note: Note
    ): String

    @Update
    @POST(WRITE_NOTE_ENDPOINT) // form name
    suspend fun updateNote(
        @Tag updateKey: String,
        @Body note: Note
    ): String

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
    suspend fun addNoteToInvalidSheet(@Body note: Note): Note
}