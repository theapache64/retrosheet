package io.github.theapache64.retrosheet.core

import com.github.theapache64.expekt.should
import io.ktor.http.Parameters
import org.junit.Test

class QueryConverterTest {

    @Test
    fun `Converts both fields and values`() {
        val input = "SELECT * WHERE foo = :param1 AND bar = :param2"
        val map = mapOf(
            "foo" to "a",
            "bar" to "b",
        )

        val paramMap = Parameters.build {
            append("param1", "value1")
            append("param2", "value2")
        }
        val actualOutput = QueryConverter(input, map, paramMap).convert()
        actualOutput.should.equal("SELECT * WHERE a = 'value1' AND b = 'value2'")
    }

    @Test
    fun `Converts fields only`() {
        val input = "SELECT * WHERE foo = 'arg1' AND bar = 'arg2'"
        val map = mapOf(
            "foo" to "a",
            "bar" to "b",
        )
        val actualOutput = QueryConverter(input, map, Parameters.build { }).convert()
        actualOutput.should.equal("SELECT * WHERE a = 'arg1' AND b = 'arg2'")
    }


    @Test
    fun `Converts values only`() {
        val input = "SELECT * WHERE foo = :arg1 AND bar = :arg2"

        val paramMap = Parameters.build {
            append("arg1", "a")
            append("arg2", "b")
        }

        val actualOutput = QueryConverter(input, emptyMap(), paramMap).convert()
        actualOutput.should.equal("SELECT * WHERE foo = 'a' AND bar = 'b'")
    }

    @Test
    fun `Strings are surrounded by single quotes and numbers are surrounded by nothing`() {
        val input =
            "SELECT * WHERE my_string = :my_string AND my_int = :my_int AND my_double = :my_double"
        val paramMap = Parameters.build {
            append("my_string", "com.truecaller")
            append("my_int", "3")
            append("my_double", "3.14")
        }

        val actualOutput = QueryConverter(input, emptyMap(), paramMap).convert()
        actualOutput.should.equal("SELECT * WHERE my_string = 'com.truecaller' AND my_int = 3 AND my_double = 3.14")
    }
}