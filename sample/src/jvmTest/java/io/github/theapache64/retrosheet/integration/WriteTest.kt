package io.github.theapache64.retrosheet.integration

import com.github.theapache64.expekt.should
import io.github.theapache64.retrosheet.util.runBlockingTest
import io.github.theapache64.retrosheetsample.AddNoteRequest
import io.github.theapache64.retrosheetsample.createNotesApi
import java.util.Date
import java.util.UUID
import org.junit.Test

class WriteTest {

    private val notesApi = createNotesApi()

    @Test
    fun `Writes data`() = runBlockingTest {
        //Write data
        val request = AddNoteRequest("Titlé - ${UUID.randomUUID()}", "Dynámic Desc 1: ${Date()}")
        notesApi.addNote(request)

        // Read data
        val remoteNote = notesApi.getNote(request.title)
        remoteNote.should.not.`null`
        remoteNote.title.should.equal(request.title)
        remoteNote.description.should.equal(request.description)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Fails to write into a invalid sheet`() = runBlockingTest {
        notesApi.addNoteToInvalidSheet(
            AddNoteRequest("some title", "Dynamic Desc 1: ${Date()}")
        )
    }
}