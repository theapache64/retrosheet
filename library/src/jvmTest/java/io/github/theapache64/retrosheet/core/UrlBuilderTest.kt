package io.github.theapache64.retrosheet.core

import org.junit.Test

class UrlBuilderTest {
    @Test
    fun test() {

        // TODO: Write tests for this.
        //  Find how to mock annotations.
        // Mocking method object
        /*val fakeMethod = mock<Method> {
            val fakeRead = mock<Read> {
                on { query } doReturn "SELECT * WHERE foo = :bar"
            }
            on { getAnnotation(Read::class.java) } doReturn fakeRead
        }

        val fakeInv = Invocation.of(fakeMethod, listOf<Any>())

        val request = Request.Builder()
            .url("https://docs.google.com/spreadsheets/d/1eDOjClNJGgrROftn9zW69WKNOnQVor_zrF8yo0v5KGs/")
            .tag(fakeInv)
            .build()

        val docId = "1eDOjClNJGgrROftn9zW69WKNOnQVor_zrF8yo0v5KGs"
        val params = "?foo=bar"
        val queryMap = mapOf(
            "foo" to "bar"
        )
        val urlBuilder = UrlBuilder(request, docId, "my_sheet", params, queryMap)
        println(urlBuilder.build())*/
    }
}