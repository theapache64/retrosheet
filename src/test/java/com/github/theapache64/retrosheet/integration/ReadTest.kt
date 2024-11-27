package com.github.theapache64.retrosheet.integration

import com.github.theapache64.expekt.should
import com.github.theapache64.retrosheet.sample.notes.Notes
import com.github.theapache64.retrosheet.util.runBlockingTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import okio.IOException
import org.junit.Test

@ExperimentalCoroutinesApi
class ReadTest {

    private val notesApi = Notes.createApi()

    @Test
    fun `Reads data`() = runBlockingTest {
        notesApi.getNote("Do not delete this row").description.should
            .equal("This is custóm desc")
        Unit
    }

    @Test
    fun `Reads list of data`() = runBlockingTest {
        notesApi.getNotes().size.should.above(1)
        Unit
    }

    @Test(expected = IOException::class)
    fun `Fails to read from an invalid sheet`() = runBlocking {
        notesApi.getNotesFromInvalidSheet()
        Unit
    }

}