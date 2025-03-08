package com.github.theapache64.retrosheet.utils

import com.github.theapache64.expekt.should
import de.siegmar.fastcsv.reader.MalformedCsvException
import kotlinx.serialization.json.Json
import org.junit.Test

class CsvConverterTest {
    @Test
    fun `Converts valid CSV`() {
        val fakeCsvData = """
            user,age
            jake,31
            adam,30
            bob,20
        """.trimIndent()

        val actualOutput = CsvConverter.convertCsvToJson(fakeCsvData, true, Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
        val expectedOutput = """[{"user":"jake","age":31},{"user":"adam","age":30},{"user":"bob","age":20}]"""
        actualOutput.should.equal(expectedOutput)
    }

    @Test(expected = MalformedCsvException::class)
    fun `Fails to convert invalid CSV`() {
        val fakeCsvData = """
            user,age
            jake
        """.trimIndent()

        CsvConverter.convertCsvToJson(fakeCsvData, true, Json {
            ignoreUnknownKeys = true
            isLenient = true
        })
    }
}