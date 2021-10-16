package com.github.theapache64.retrosheet.core

import com.github.theapache64.expekt.should
import org.junit.Test

class QueryConverterTest {

    @Test
    fun `Converts both fields and values`() {
        val input = "SELECT * WHERE foo = :param1 AND bar = :param2"
        val map = mapOf(
            "foo" to "a",
            "bar" to "b",
        )
        val paramMap = mapOf(
            "param1" to "value1",
            "param2" to "value2"
        )
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
        val actualOutput = QueryConverter(input, map, emptyMap()).convert()
        actualOutput.should.equal("SELECT * WHERE a = 'arg1' AND b = 'arg2'")
    }


    @Test
    fun `Converts values only`() {
        val input = "SELECT * WHERE foo = :arg1 AND bar = :arg2"
        val paramMap = mapOf(
            "arg1" to "a",
            "arg2" to "b"
        )
        val actualOutput = QueryConverter(input, emptyMap(), paramMap).convert()
        actualOutput.should.equal("SELECT * WHERE foo = 'a' AND bar = 'b'")
    }
}