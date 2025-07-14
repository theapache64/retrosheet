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
    fun `Update data`() = runBlockingTest {

        val notesApi = createNotesApi()

        //Add data
        val note = Note("Titlé - ${UUID.randomUUID()}", "Dynámic Desc 1: ${Date()}")
        val updateKey = notesApi.addNoteForUpdate(note)
        updateKey.length.should.be.above(0)

        // Read data
        val remoteNote = notesApi.findNoteByTitle(note.title)
        remoteNote.should.not.`null`
        remoteNote.title.should.equal(note.title)
        remoteNote.description.should.equal(note.description)

        // Edit data
        val updatedNote = Note("Titlé - ${UUID.randomUUID()}", "Dynámic Desc 1: ${Date()}")
        notesApi.updateNote(updateKey, updatedNote)

        // Confirm old data is not there
        val oldNoteResult = try {
            notesApi.findNoteByTitle(note.title)
        } catch (_: NoSuchElementException) {
            null
        }
        oldNoteResult.should.`null`

        // Confirm updated data
        val updatedRemoteNote = notesApi.findNoteByTitle(updatedNote.title)
        updatedRemoteNote.should.not.`null`
        updatedRemoteNote.title.should.equal(updatedRemoteNote.title)
        updatedRemoteNote.description.should.equal(updatedRemoteNote.description)
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