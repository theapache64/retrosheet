package com.theapache64.retrosheet.core

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 23:01
 */
class SheetVerifier(
    private val map: Map<String, String>
) {
    companion object {
        val RESERVED_WORDS = listOf(
            "and",
            "asc",
            "by",
            "date",
            "datetime",
            "desc",
            "false",
            "format",
            "group",
            "label",
            "limit",
            "not",
            "offset",
            "options",
            "or",
            "order",
            "pivot",
            "select",
            "timeofday",
            "timestamp",
            "true",
            "where"
        )

        val RESERVED_CHARS = listOf(':')
    }

    @Throws(IllegalArgumentException::class)
    fun verify(): Boolean {

        // Checking if there's any reserved column name
        for (entry in map) {
            val isReservedWord = RESERVED_WORDS.contains(entry.key.toLowerCase())
            require(!isReservedWord) { "'${entry.key}' can't be a column name. It's a reserved word" }
        }

        // Checking if there's any reserved chars
        for (entry in map) {
            val reservedChar = RESERVED_CHARS.find { entry.key.contains(it) }
            require(reservedChar == null) { "'${entry.key}' can't contain '$reservedChar'. It's a reserved char" }
        }

        return true
    }
}