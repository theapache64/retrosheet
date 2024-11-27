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
        val request = AddNoteRequest("Titlé - ${java.util.UUID.randomUUID()}", "Dynámic Desc 1: ${Date()}")
        notesApi.addNote(request)

        // Read data
        val remoteNote = notesApi.getNote(request.title)
        remoteNote.should.not.`null`
        remoteNote.title.should.equal(request.title)
        remoteNote.description.should.equal(request.description)
    }

    @Test(expected = IOException::class)
    fun `Fails to write into a invalid sheet`() = runBlockingTest {
        notesApi.addNoteToInvalidSheet(
            AddNoteRequest("some title", "Dynamic Desc 1: ${Date()}")
        )
    }
}