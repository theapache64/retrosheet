package com.github.theapache64.retrosheet.integration

import com.github.theapache64.expekt.should
import com.github.theapache64.retrosheet.sample.notes.AddNoteRequest
import com.github.theapache64.retrosheet.sample.notes.Notes
import com.github.theapache64.retrosheet.util.runBlockingTest
import java.util.Date
import okio.IOException
import org.junit.Test

class WriteTest {

    private val notesApi = Notes.createApi()

    @Test
    fun `Writes data`() = runBlockingTest {
        //Write data
        val noteTitle = "Title - ${java.util.UUID.randomUUID()}"
        notesApi.addNote(
            AddNoteRequest(noteTitle, "Dynamic Desc 1: ${Date()}")
        )

        // Read data
        notesApi.getNote(noteTitle).should.not.`null`
    }

    @Test(expected = IOException::class)
    fun `Fails to write into a invalid sheet`() = runBlockingTest {
        notesApi.addNoteToInvalidSheet(
            AddNoteRequest("some title", "Dynamic Desc 1: ${Date()}")
        )
    }
}