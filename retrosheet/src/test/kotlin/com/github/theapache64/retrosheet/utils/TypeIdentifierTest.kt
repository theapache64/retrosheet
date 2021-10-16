package com.github.theapache64.retrosheet.utils

import com.github.theapache64.expekt.should
import org.junit.Test

class TypeIdentifierTest {

    @Test
    fun `Valid double`() {
        TypeIdentifier.isDouble("2.65656").should.`true`
        TypeIdentifier.isDouble("2.6").should.`true`
    }

    @Test
    fun `Invalid double`() {
        TypeIdentifier.isDouble("2").should.`false`
    }

    @Test
    fun `Valid integer`() {
        TypeIdentifier.isInteger("2").should.`true`
        TypeIdentifier.isInteger("0").should.`true`
        TypeIdentifier.isInteger("-1").should.`true`
    }

    @Test
    fun `Invalid integer`() {
        TypeIdentifier.isInteger("2.0").should.`false`
    }

    @Test
    fun `Valid boolean`() {
        TypeIdentifier.isBoolean("false").should.`true`
        TypeIdentifier.isBoolean("true").should.`true`
        TypeIdentifier.isBoolean("TRUE").should.`true`
        TypeIdentifier.isBoolean("FALSE").should.`true`
        TypeIdentifier.isBoolean("faLSe").should.`true`
        TypeIdentifier.isBoolean("tRue").should.`true`
    }

    @Test
    fun `Invalid boolean`() {
        TypeIdentifier.isBoolean("treu").should.`false`
        TypeIdentifier.isBoolean("fals").should.`false`
        TypeIdentifier.isBoolean("Fálse").should.`false`
    }
}