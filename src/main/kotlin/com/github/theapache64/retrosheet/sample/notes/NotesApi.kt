package com.github.theapache64.retrosheet.sample.notes

import com.github.theapache64.retrosheet.core.Read
import com.github.theapache64.retrosheet.core.Write
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface NotesApi {

    @Read("SELECT *")
    @GET("notes") // sheet name
    suspend fun getNotes(): List<Note>

    @Write
    @POST(ADD_NOTE_ENDPOINT) // form name
    suspend fun addNote(@Body addNoteRequest: AddNoteRequest): AddNoteRequest
}
