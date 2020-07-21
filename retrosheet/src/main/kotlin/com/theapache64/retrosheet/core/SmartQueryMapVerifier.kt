package com.theapache64.retrosheet.core

/**
 * Created by theapache64 : Jul 21 Tue,2020 @ 23:01
 */
class SmartQueryMapVerifier(
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
    }

    @Throws(IllegalArgumentException::class)
    fun verify(): Boolean {
        for (entry in map) {
            val isReservedWord = RESERVED_WORDS.contains(entry.key.toLowerCase())
            require(!isReservedWord) { "'${entry.key}' can't be a column name. It's a reserved word" }
        }
        return true
    }
}