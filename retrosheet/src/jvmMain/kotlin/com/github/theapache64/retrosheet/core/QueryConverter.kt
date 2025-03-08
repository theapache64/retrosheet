package com.github.theapache64.retrosheet.core

import com.github.theapache64.retrosheet.utils.TypeIdentifier

/**
 * To create final query needed for the public sheet API.
 *
 * @property smartQuery The query
 * @property smartQueryMap field map
 * @property paramMap value map
 */
class QueryConverter(
    private val smartQuery: String,
    private val smartQueryMap: Map<String, String>,
    private val paramMap: Map<String, String>?
) {
    /**
     * To generate final query.
     * @return [smartQuery] replaced with [smartQueryMap] (fields) and [paramMap] (values)
     */
    fun convert(): String {
        var outputQuery = smartQuery
        // Replacing values
        paramMap?.let {
            for (entry in paramMap.entries) {
                val value = sanitizeValue(entry.value)
                outputQuery = outputQuery.replace(":${entry.key}", value)
            }
        }

        // Replacing keys
        for (entry in smartQueryMap) {
            outputQuery = outputQuery.replace(entry.key, entry.value)
        }
        return outputQuery
    }

    private fun sanitizeValue(value: String): String {
        return if (TypeIdentifier.isNumber(value)) {
            value
        } else {
            "'$value'"
        }
    }
}
