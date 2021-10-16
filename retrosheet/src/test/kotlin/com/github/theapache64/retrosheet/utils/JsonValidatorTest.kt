package com.github.theapache64.retrosheet.utils

import com.github.theapache64.expekt.should
import org.junit.Test

class JsonValidatorTest {
    @Test
    fun `Valid simple JSON`() {
        JsonValidator.isValidJsonObject(
            """{ "name": "Jake", "age" : 31 }"""
        ).should.`true`
    }

    @Test
    fun `Valid complex JSON`() {
        val complexJson = """
            {
              "accounting": [
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "age": 23
                },
                {
                  "firstName": "Mary",
                  "lastName": "Smith",
                  "age": 32
                }
              ],
              "sales": [
                {
                  "firstName": "Sally",
                  "lastName": "Green",
                  "age": 27
                },
                {
                  "firstName": "Jim",
                  "lastName": "Galley",
                  "age": 41
                }
              ]
            } 
        """.trimIndent()
        JsonValidator.isValidJsonObject(complexJson).should.`true`
    }

    @Test
    fun `Invalid JSON`() {
        JsonValidator.isValidJsonObject(
            """{ "age : 31 }"""
        ).should.`false`
    }
}