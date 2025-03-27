@file:OptIn(ExperimentalSerializationApi::class)

package com.github.theapache64.retrosheet.utils

import de.siegmar.fastcsv.reader.NamedCsvReader
import kotlin.reflect.KType
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.csv.Csv
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.serializer

/**
 * Created by theapache64 : Jul 22 Wed,2020 @ 00:05
 */
internal object CsvConverter {

    private val csv = Csv {
        hasHeaderRecord = true
    }

    fun convertCsvToModel(kType: KType, csvData: String): Any? {
        return csv.decodeFromString(serializer(kType), csvData)
    }

    @OptIn(ExperimentalSerializationApi::class)
    fun convertCsvToJson(
        csvData: String,
        isReturnTypeList: Boolean,
        json: Json
    ): String? {
        val items = mutableListOf<JsonObject>()
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
