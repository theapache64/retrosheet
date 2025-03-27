package com.github.theapache64.retrosheet.utils

import com.github.theapache64.expekt.should
import kotlin.reflect.typeOf
import kotlinx.serialization.MissingFieldException
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.junit.Test

@Serializable
data class Person(
    val username: String,
    val age: Int
)

@Serializable
data class Note(
    @SerialName("created_at")
    val createdAt: String,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String
)

class CsvConverterTest {


    @Test
    fun `Converts valid CSV to model`() {
        val fakeCsvData = """
            username,age
            jake,31
            adam,30
            bob,20
        """.trimIndent()

        val actualOutput = CsvConverter.convertCsvToModel(typeOf<List<Person>>(), fakeCsvData)
        val expectedOutput = listOf(
            Person("jake", 31),
            Person("adam", 30),
            Person("bob", 20)
        )
        actualOutput.should.equal(expectedOutput)
    }

    @Test
    fun `Converts single CSV to model`() {
        val fakeCsvData = """
            username,age
            jake,31
        """.trimIndent()

        val actualOutput = CsvConverter.convertCsvToModel(typeOf<Person>(), fakeCsvData)
        val expectedOutput = Person("jake", 31)
        actualOutput.should.equal(expectedOutput)
    }


    @Test
    fun `Converts real CSV to model`() {
        val fakeCsvData = """
            "created_at","title","description"
            "10/16/2021 2:53:55","Do not delete this row","This is custóm desc"
            "10/16/2021 2:55:01","Do not delete this also","This is custóm desc"
            "11/27/2024 23:36:49","Dynamic Note 1","Dynmic Desc 1: Wed Nov 27 23:36:47 IST 2024"
        """.trimIndent()

        val actualOutput = CsvConverter.convertCsvToModel(typeOf<List<Note>>(), fakeCsvData)
        val expectedOutput = listOf(
            Note("10/16/2021 2:53:55", "Do not delete this row", "This is custóm desc"),
            Note("10/16/2021 2:55:01", "Do not delete this also", "This is custóm desc"),
            Note("11/27/2024 23:36:49", "Dynamic Note 1", "Dynmic Desc 1: Wed Nov 27 23:36:47 IST 2024")
        )
        actualOutput.should.equal(expectedOutput)
    }


    @Test(expected = MissingFieldException::class)
    fun `Fails to convert invalid CSV`() {
        val fakeCsvData = """
            username,age
            jake
        """.trimIndent()

        CsvConverter.convertCsvToModel(typeOf<Person>(), fakeCsvData)
    }
}