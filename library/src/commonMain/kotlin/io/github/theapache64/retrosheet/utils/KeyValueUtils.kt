package io.github.theapache64.retrosheet.utils

import io.github.theapache64.retrosheet.exception.InvalidKeyValueFormat


internal object KeyValueUtils {

    /**
     * To transform given CSV's columns to rows
     *
     * @param responseBody CSV
     * @return transformed CSV
     */
    fun transform(responseBody: String): String {
        try {
            val keyValueList = responseBody
                .trim()
                .split("\n")
                .drop(1) // header
                .map { it.split(",") }
                .map {
                    Pair(it[0].trim(), it[1].trim())
                }

            val keys = keyValueList.map { it.first }
            val values = keyValueList.map { it.second }

            return keys.joinToString(",") + "\n" + values.joinToString(",")
        } catch (e: IndexOutOfBoundsException) {
            throw InvalidKeyValueFormat("Invalid key-value format: $responseBody - cause: ${e.message}")
        }
    }
}
