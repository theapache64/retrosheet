package com.github.theapache64.retrosheet.core

import com.github.theapache64.expekt.should
import org.junit.Test

class SheetVerifierTest {
    @Test
    fun `Valid sheet`() {
        SheetVerifier(
            setOf("foo", "bar")
        ).verify().should.`true`
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Invalid sheet`() {
        SheetVerifier(
            setOf("AND", "SELECT")
        ).verify()
    }
}