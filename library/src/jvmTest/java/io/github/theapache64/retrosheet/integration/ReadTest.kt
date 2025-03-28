package io.github.theapache64.retrosheet.integration

import com.github.theapache64.expekt.should
import io.github.theapache64.retrosheet.util.runBlockingTest
import io.github.theapache64.retrosheetsample.createNotesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class ReadTest {

    private val notesApi = createNotesApi()

    @Test
    fun `Reads data`() = runBlockingTest {
        notesApi.getNote("Do not delete this row").description.should
            .equal("This is custóm desc")
        Unit
    }

    @Test
    fun `Reads list of data`() = runBlockingTest {
        notesApi.getLastFiveItems().size.should.above(1)
        Unit
    }

    @Test(expected = IllegalStateException::class)
    fun `Fails to read from an invalid sheet`() = runBlocking {
        notesApi.getNotesFromInvalidSheet()
        Unit
    }

}