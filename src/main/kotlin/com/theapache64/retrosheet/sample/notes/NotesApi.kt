package com.theapache64.retrosheet.sample.notes

import com.theapache64.retrofit.calladapter.flow.Resource
import com.theapache64.retrosheet.core.Write
import com.theapache64.retrosheet.core.Read
import kotlinx.coroutines.flow.Flow
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
