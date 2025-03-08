package com.github.theapache64.retrosheet.utils

import com.github.theapache64.expekt.should
import com.github.theapache64.retrosheet.exception.InvalidKeyValueFormat
import org.junit.Test


internal class KeyValueUtilsTest {
    @Test
    fun testTransform() {
        val input = """
            "key","value"
            "total_products","99"
            "products_per_page","10"
            "total_pages","10"
            "currency","$"
            "delivery_charge","40"
        """.trimIndent()

        val expectedOutput = """
            "total_products","products_per_page","total_pages","currency","delivery_charge"
            "99","10","10","$","40"
        """.trimIndent()

        val actualOutput = KeyValueUtils.transform(input)

        actualOutput.should.equal(expectedOutput)
    }

    @Test
    @Throws(InvalidKeyValueFormat::class)
    fun testException() {
        val input = """
            "key","value"
            "total_products"
        """.trimIndent()

        try {
            KeyValueUtils.transform(input)
            assert(false)
        } catch (e: InvalidKeyValueFormat) {
            assert(true)
        }
    }
}