@file:OptIn(ExperimentalSerializationApi::class)

package com.github.theapache64.retrosheet.utils

import de.siegmar.fastcsv.reader.NamedCsvReader
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

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
        val items = mutableListOf<JsonObject>()
        println("QuickTag: CsvConverter:convertCsvToJson: '$csvData'")
        NamedCsvReader.builder()
            .build(csvData)
            .forEach { row ->
                val item = buildJsonObject {
                    for ((header, value) in row.fields) {
                        when {
                            TypeIdentifier.isInteger(value) -> {
                                put(header, JsonPrimitive(value.toLong()))
                            }

                            TypeIdentifier.isBoolean(value) -> {
                                put(header, JsonPrimitive(value.toBoolean()))
                            }

                            TypeIdentifier.isDouble(value) -> {
                                put(header, JsonPrimitive(value.toDouble()))
                            }
                            else -> {
                                if (value.isNullOrBlank()) {
                                    put(header, JsonPrimitive(null))
                                } else {
                                    put(header, JsonPrimitive(value))
                                }
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
