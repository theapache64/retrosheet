package io.github.theapache64.retrosheet.integration

import com.github.theapache64.expekt.should
import io.github.theapache64.retrosheet.util.runBlockingTest
import io.github.theapache64.retrosheetsample.Note
import io.github.theapache64.retrosheetsample.createNotesApi
import java.util.*
import org.junit.Test

class WriteTest {


    @Test
    fun `Writes data`() = runBlockingTest {

        val notesApi = createNotesApi()

        //Write data
        val newNote = Note("Titlé - ${UUID.randomUUID()}", "Dynámic Desc 1: ${Date()}")
        notesApi.addNote(newNote)

        // Read data
        val remoteNote = notesApi.findNoteByTitle(newNote.title)
        remoteNote.should.not.`null`
        remoteNote.title.should.equal(newNote.title)
        remoteNote.description.should.equal(newNote.description)
    }

    @Test
    fun `Writes data (proxy)`() = runBlockingTest {
        val notesApi = createNotesApi {
            setUseProxyForWrite(true)
        }
        //Write data
        val newNote = Note("Titlé - ${UUID.randomUUID()}", "Dynámic Desc 1: ${Date()}")
        notesApi.addNote(newNote)

        // Read data
        val remoteNote = notesApi.findNoteByTitle(newNote.title)
        remoteNote.should.not.`null`
        remoteNote.title.should.equal(newNote.title)
        remoteNote.description.should.equal(newNote.description)
    }




    @Test(expected = IllegalArgumentException::class)
    fun `Fails to write into a invalid sheet`() = runBlockingTest {
        val notesApi = createNotesApi()
        notesApi.addNoteToInvalidSheet(
            Note("some title", "Dynamic Desc 1: ${Date()}")
        )
    }
}