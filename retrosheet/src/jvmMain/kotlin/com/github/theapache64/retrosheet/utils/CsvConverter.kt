@file:OptIn(ExperimentalSerializationApi::class)

package com.github.theapache64.retrosheet.utils

import de.siegmar.fastcsv.reader.NamedCsvReader
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:05
 */
internal object CsvConverter {
    @OptIn(ExperimentalSerializationApi::class)
    fun convertCsvToJson(
        csvData: String,
        isReturnTypeList: Boolean,
        json: Json
    ): String? {
        val items = mutableListOf<Map<String, Any?>>()

        NamedCsvReader.builder()
            .build(csvData)
            .forEach { row ->
                val item = mutableMapOf<String, Any?>().apply {
                    for ((header, value) in row.fields) {
                        when {
                            TypeIdentifier.isInteger(
                                value
                            ) -> {
                                put(header, value.toLong())
                            }

                            TypeIdentifier.isBoolean(
                                value
                            ) -> {
                                put(header, value.toBoolean())
                            }

                            TypeIdentifier.isDouble(
                                value
                            ) -> {
                                put(header, value.toDouble())
                            }

                            else -> {
                                val finalValue = if (value.isNullOrBlank()) {
                                    null
                                } else {
                                    value
                                }
                                put(header, finalValue)
                            }
                        }
                    }
                }
                items.add(item)
            }

        return when {
            isReturnTypeList -> {
                json.encodeToString(items)
            }

            items.isNotEmpty() -> {
                json.encodeToString(items.first())
            }

            else -> {
                null
            }
        }
    }
}
