package com.theapache64.retrosheet.sample.notes

import com.squareup.moshi.Moshi
import com.theapache64.retrofit.calladapter.either.EitherCallAdapterFactory
import com.theapache64.retrofit.calladapter.flow.FlowResourceCallAdapterFactory
import com.theapache64.retrosheet.RetrosheetInterceptor
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 02:11
 */
const val ADD_NOTE_ENDPOINT = "add_note"
fun main() = runBlocking {

    val retrosheetInterceptor = RetrosheetInterceptor.Builder()
        .setLogging(false)
        .addSheet("notes", "title", "description")
        .addForm(
            ADD_NOTE_ENDPOINT,
            "https://docs.google.com/forms/d/e/1FAIpQLSdmavg6P4eZTmIu-0M7xF_z-qDCHdpGebX8MGL43HSGAXcd3w/viewform?usp=sf_link"
        )
        .build()

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(retrosheetInterceptor)
        .build()


    val moshi = Moshi.Builder().build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://docs.google.com/spreadsheets/d/1YTWKe7_mzuwl7AO1Es1aCtj5S9buh3vKauKCMjx1j_M/")
        .client(okHttpClient)
        .addCallAdapterFactory(EitherCallAdapterFactory())
        .addCallAdapterFactory(FlowResourceCallAdapterFactory())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()

    val notesApi = retrofit.create(NotesApi::class.java)
    notesApi.getNotes().collect {
        println(it)
    }

    // Adding sample order
    val addNote = notesApi.addNote(
        AddNoteRequest("Dynamic Note 1", "Dynamic Desc 1")
    )

    addNote.collect {
        println(it)
    }
    Unit
}