package io.github.theapache64.retrosheet.core

import com.github.theapache64.expekt.should
import org.junit.Test

class ColumnNameVerifierTest {
    @Test
    fun `Valid sheet`() {
        ColumnNameVerifier(
            setOf("foo", "bar")
        ).verify().should.`true`
    }

    @Test(expected = IllegalArgumentException::class)
    fun `Invalid sheet`() {
        ColumnNameVerifier(
            setOf("AND", "SELECT")
        ).verify()
    }
}