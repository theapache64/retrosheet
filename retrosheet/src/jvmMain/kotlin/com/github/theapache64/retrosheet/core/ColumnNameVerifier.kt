package com.github.theapache64.retrosheet.core

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 23:01
 */
class ColumnNameVerifier(
    private val keys: Set<String>
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

        for (key in keys) {
            // Checking if there's any reserved column name
            val isReservedWord = RESERVED_WORDS.contains(key.lowercase())
            require(!isReservedWord) { "'$key' can't be a column name. It's a reserved word" }

            // Checking if there's any reserved chars
            val reservedChar = RESERVED_CHARS.find { key.contains(it) }
            require(reservedChar == null) { "'$key' can't contain '$reservedChar'. It's a reserved char" }
        }

        return true
    }
}
